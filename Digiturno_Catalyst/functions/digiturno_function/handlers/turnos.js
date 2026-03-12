'use strict';

const { getFechaHoy, getHoraAhora, getAllRows, col, calcularSegundos } = require('../lib/utils');
const { requireAgente } = require('./auth');

const PRIORIDAD_LETRAS = { discapacidad: 'D', embarazo: 'E', adulto_mayor: 'M', ninguna: 'A' };
const PRIORIDAD_ORDEN = { discapacidad: 0, embarazo: 1, adulto_mayor: 2, ninguna: 3 };

async function generarTurno(app, body) {
  const { sede_id, servicio_id, prioridad = 'ninguna', nombre_cliente = '', cedula = '', telefono = '' } = body || {};
  if (!sede_id) throw { status: 400, message: 'sede_id es requerido' };
  if (!servicio_id) throw { status: 400, message: 'servicio_id es requerido' };

  const prioridadNorm = PRIORIDAD_LETRAS[prioridad] ? prioridad : 'ninguna';
  const letraPrefijo = PRIORIDAD_LETRAS[prioridadNorm];
  const fecha = getFechaHoy();
  const datastore = app.datastore();

  const tablaSedes = datastore.table('Sede');
  let sede;
  try { sede = await tablaSedes.getRow(String(sede_id)); } catch (e) { /* */ }
  if (!sede) throw { status: 404, message: 'Sede no encontrada' };

  const tablaServicios = datastore.table('Servicio');
  let servicio;
  try { servicio = await tablaServicios.getRow(String(servicio_id)); } catch (e) { /* */ }
  if (!servicio) throw { status: 404, message: 'Servicio no encontrado' };

  const tablaTurnos = datastore.table('Turno');
  const turnosAll = await getAllRows(tablaTurnos);
  const turnosHoy = turnosAll.filter(t =>
    String(col(t, 'sede_id')) === String(sede_id) &&
    col(t, 'fecha_turno') === fecha
  );

  const maxSecuencial = turnosHoy.reduce((max, t) => {
    const n = Number(col(t, 'numero_secuencial')) || 0;
    return n > max ? n : max;
  }, 0);

  const siguienteNumero = maxSecuencial + 1;
  const codigo = `${letraPrefijo}-${String(siguienteNumero).padStart(3, '0')}`;

  const turno = await tablaTurnos.insertRow({
    sede_id: String(sede_id),
    servicio_id: String(servicio_id),
    numero_turno: codigo,
    llamadas: 0,
    numero_secuencial: siguienteNumero,
    estado: 'espera',
    fecha_turno: fecha,
    hora_generado: getHoraAhora(),
    hora_llamado: '',
    hora_inicio_atencion: '',
    hora_fin_atencion: '',
    tiempo_espera_seg: 0,
    tiempo_atencion_seg: 0,
    nombre_cliente: String(nombre_cliente).trim().slice(0, 200),
    cedula: String(cedula).trim().slice(0, 20),
    telefono: String(telefono).trim().slice(0, 20),
    prioridad: prioridadNorm
  });

  const enCola = turnosHoy.filter(t =>
    String(col(t, 'servicio_id')) === String(servicio_id) &&
    col(t, 'estado') === 'espera'
  ).length + 1;

  return {
    turno: {
      ROWID: turno.ROWID,
      numero_turno: codigo,
      numero_secuencial: siguienteNumero,
      servicio_id: String(servicio_id),
      servicio_nombre: col(servicio, 'nombre'),
      sede_id: String(sede_id),
      sede_nombre: col(sede, 'nombre'),
      prioridad: prioridadNorm,
      estado: 'espera',
      posicion_en_cola: enCola,
      tiempo_estimado_min: enCola * 5
    }
  };
}

async function llamarSiguiente(app, body) {
  const usuario = await requireAgente(app);
  const { servicio_id } = body || {};
  if (!servicio_id) throw { status: 400, message: 'servicio_id es requerido' };

  const sedeId = usuario.sede_id;
  if (!sedeId) throw { status: 400, message: 'Agente no tiene sede asignada' };

  if (usuario.rol !== 'admin' && usuario.servicios_asignados.length > 0) {
    if (!usuario.servicios_asignados.includes(String(servicio_id))) {
      throw { status: 403, message: 'No tiene asignado este servicio' };
    }
  }

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');
  const fecha = getFechaHoy();

  const turnosAll = await getAllRows(tablaTurnos);
  const turnoActivo = turnosAll.find(t =>
    String(col(t, 'agente_id')) === String(usuario.perfil_id) &&
    col(t, 'fecha_turno') === fecha &&
    ['llamado', 'en_atencion'].includes(col(t, 'estado'))
  );

  if (turnoActivo) {
    throw { status: 400, message: 'Ya tiene un turno activo. Finalícelo o márquelo como no presentado.' };
  }

  const pendientes = turnosAll
    .filter(t =>
      String(col(t, 'sede_id')) === String(sedeId) &&
      String(col(t, 'servicio_id')) === String(servicio_id) &&
      col(t, 'fecha_turno') === fecha &&
      col(t, 'estado') === 'espera'
    )
    .sort((a, b) => {
      const pa = PRIORIDAD_ORDEN[col(a, 'prioridad') || 'ninguna'] ?? 3;
      const pb = PRIORIDAD_ORDEN[col(b, 'prioridad') || 'ninguna'] ?? 3;
      if (pa !== pb) return pa - pb;
      return (Number(col(a, 'numero_secuencial')) || 0) - (Number(col(b, 'numero_secuencial')) || 0);
    });

  if (pendientes.length === 0) {
    return { turno: null, message: 'No hay turnos en espera para este servicio' };
  }

  const siguiente = pendientes[0];
  const horaLlamado = getHoraAhora();

  await tablaTurnos.updateRow({
    ROWID: siguiente.ROWID,
    estado: 'llamado',
    agente_id: usuario.perfil_id,
    llamadas: 1,
    hora_llamado: horaLlamado,
    tiempo_espera_seg: calcularSegundos(col(siguiente, 'hora_generado'), horaLlamado)
  });

  return {
    turno: {
      ROWID: siguiente.ROWID,
      numero_turno: col(siguiente, 'numero_turno'),
      numero_secuencial: Number(col(siguiente, 'numero_secuencial')),
      prioridad: col(siguiente, 'prioridad') || 'ninguna',
      nombre_cliente: col(siguiente, 'nombre_cliente') || '',
      cedula: col(siguiente, 'cedula') || '',
      telefono: col(siguiente, 'telefono') || '',
      servicio_id: col(siguiente, 'servicio_id'),
      estado: 'llamado',
      llamadas: 1,
      modulo_atencion: usuario.modulo_atencion
    }
  };
}

async function rellamarTurno(app, body) {
  const usuario = await requireAgente(app);
  const { turno_id } = body || {};
  if (!turno_id) throw { status: 400, message: 'turno_id es requerido' };

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');

  let turno;
  try { turno = await tablaTurnos.getRow(String(turno_id)); } catch (e) { /* */ }
  if (!turno) throw { status: 404, message: 'Turno no encontrado' };

  if (String(col(turno, 'agente_id')) !== String(usuario.perfil_id)) {
    throw { status: 403, message: 'Este turno no le pertenece' };
  }
  if (col(turno, 'estado') !== 'llamado') {
    throw { status: 400, message: 'Solo se puede rellamar un turno en estado llamado' };
  }

  const llamadasActuales = Number(col(turno, 'llamadas')) || 1;

  if (llamadasActuales >= 3) {
    await tablaTurnos.updateRow({
      ROWID: turno_id,
      estado: 'no_se_presento',
      hora_fin_atencion: getHoraAhora()
    });
    return { turno: null, message: 'Máximo de re-llamadas alcanzado (3). Turno marcado como no se presentó.' };
  }

  await tablaTurnos.updateRow({
    ROWID: turno_id,
    llamadas: llamadasActuales + 1,
    hora_llamado: getHoraAhora()
  });

  return {
    turno: {
      ROWID: turno.ROWID,
      numero_turno: col(turno, 'numero_turno'),
      llamadas: llamadasActuales + 1,
      estado: 'llamado'
    },
    message: `Re-llamada ${llamadasActuales + 1} de 3`
  };
}

async function iniciarAtencion(app, body) {
  const usuario = await requireAgente(app);
  const { turno_id } = body || {};
  if (!turno_id) throw { status: 400, message: 'turno_id es requerido' };

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');

  let turno;
  try { turno = await tablaTurnos.getRow(String(turno_id)); } catch (e) { /* */ }
  if (!turno) throw { status: 404, message: 'Turno no encontrado' };

  if (String(col(turno, 'agente_id')) !== String(usuario.perfil_id)) {
    throw { status: 403, message: 'Este turno no le pertenece' };
  }
  if (col(turno, 'estado') !== 'llamado') {
    throw { status: 400, message: 'Solo se puede atender un turno en estado llamado' };
  }

  await tablaTurnos.updateRow({
    ROWID: turno_id,
    estado: 'en_atencion',
    hora_inicio_atencion: getHoraAhora()
  });

  return {
    turno: { ROWID: turno.ROWID, numero_turno: col(turno, 'numero_turno'), estado: 'en_atencion' }
  };
}

async function finalizarTurno(app, body) {
  const usuario = await requireAgente(app);
  const { turno_id } = body || {};
  if (!turno_id) throw { status: 400, message: 'turno_id es requerido' };

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');

  let turno;
  try { turno = await tablaTurnos.getRow(String(turno_id)); } catch (e) { /* */ }
  if (!turno) throw { status: 404, message: 'Turno no encontrado' };

  if (String(col(turno, 'agente_id')) !== String(usuario.perfil_id)) {
    throw { status: 403, message: 'Este turno no le pertenece' };
  }

  const estado = col(turno, 'estado');
  if (!['llamado', 'en_atencion'].includes(estado)) {
    throw { status: 400, message: 'Solo se puede finalizar un turno llamado o en atención' };
  }

  const horaFin = getHoraAhora();
  const horaInicio = col(turno, 'hora_inicio_atencion') || col(turno, 'hora_llamado');
  const tiempoAtencion = calcularSegundos(horaInicio, horaFin);

  await tablaTurnos.updateRow({
    ROWID: turno_id,
    estado: 'finalizado',
    hora_fin_atencion: horaFin,
    tiempo_atencion_seg: tiempoAtencion
  });

  try {
    const tablaSesiones = datastore.table('SesionAgente');
    const sesiones = await getAllRows(tablaSesiones);
    const sesionActiva = sesiones.find(s =>
      String(col(s, 'agente_id')) === String(usuario.perfil_id) && !col(s, 'hora_fin')
    );
    if (sesionActiva) {
      await tablaSesiones.updateRow({
        ROWID: sesionActiva.ROWID,
        turnos_atendidos: (Number(col(sesionActiva, 'turnos_atendidos')) || 0) + 1
      });
    }
  } catch (e) { /* sesiones opcionales */ }

  return {
    turno: {
      ROWID: turno.ROWID,
      numero_turno: col(turno, 'numero_turno'),
      estado: 'finalizado',
      tiempo_atencion_seg: tiempoAtencion
    }
  };
}

async function noSePresento(app, body) {
  const usuario = await requireAgente(app);
  const { turno_id } = body || {};
  if (!turno_id) throw { status: 400, message: 'turno_id es requerido' };

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');

  let turno;
  try { turno = await tablaTurnos.getRow(String(turno_id)); } catch (e) { /* */ }
  if (!turno) throw { status: 404, message: 'Turno no encontrado' };

  if (String(col(turno, 'agente_id')) !== String(usuario.perfil_id)) {
    throw { status: 403, message: 'Este turno no le pertenece' };
  }

  await tablaTurnos.updateRow({
    ROWID: turno_id,
    estado: 'no_se_presento',
    hora_fin_atencion: getHoraAhora()
  });

  return {
    turno: { ROWID: turno.ROWID, numero_turno: col(turno, 'numero_turno'), estado: 'no_se_presento' }
  };
}

async function obtenerMiTurno(app, query) {
  const turno_id = query?.turno_id;
  if (!turno_id) throw { status: 400, message: 'turno_id es requerido' };

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');

  let turno;
  try { turno = await tablaTurnos.getRow(String(turno_id)); } catch (e) { /* */ }
  if (!turno) throw { status: 404, message: 'Turno no encontrado' };

  const estado = col(turno, 'estado');
  let posicion = 0;
  let agente_modulo = '';

  if (estado === 'espera') {
    const turnosAll = await getAllRows(tablaTurnos);
    posicion = turnosAll.filter(t =>
      String(col(t, 'sede_id')) === String(col(turno, 'sede_id')) &&
      String(col(t, 'servicio_id')) === String(col(turno, 'servicio_id')) &&
      col(t, 'fecha_turno') === col(turno, 'fecha_turno') &&
      col(t, 'estado') === 'espera' &&
      (Number(col(t, 'numero_secuencial')) || 0) < (Number(col(turno, 'numero_secuencial')) || 0)
    ).length;
  }

  if (['llamado', 'en_atencion'].includes(estado)) {
    try {
      const tablaUsuarios = datastore.table('Usuario');
      const agente = await tablaUsuarios.getRow(String(col(turno, 'agente_id')));
      agente_modulo = col(agente, 'modulo_atencion') || '';
    } catch (e) { /* */ }
  }

  return {
    turno: {
      ROWID: turno.ROWID,
      numero_turno: col(turno, 'numero_turno'),
      estado,
      posicion_en_cola: posicion,
      tiempo_estimado_min: posicion * 5,
      agente_modulo
    }
  };
}

async function obtenerTurnoActivo(app) {
  const usuario = await requireAgente(app);
  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');
  const fecha = getFechaHoy();

  const turnosAll = await getAllRows(tablaTurnos);
  const activo = turnosAll.find(t =>
    String(col(t, 'agente_id')) === String(usuario.perfil_id) &&
    col(t, 'fecha_turno') === fecha &&
    ['llamado', 'en_atencion'].includes(col(t, 'estado'))
  );

  if (!activo) return { turno: null };

  let servicio_nombre = '';
  try {
    const tablaServicios = datastore.table('Servicio');
    const servicio = await tablaServicios.getRow(String(col(activo, 'servicio_id')));
    servicio_nombre = col(servicio, 'nombre') || '';
  } catch (e) { /* */ }

  return {
    turno: {
      ROWID: activo.ROWID,
      numero_turno: col(activo, 'numero_turno'),
      estado: col(activo, 'estado'),
      llamadas: Number(col(activo, 'llamadas')) || 0,
      nombre_cliente: col(activo, 'nombre_cliente') || '',
      cedula: col(activo, 'cedula') || '',
      telefono: col(activo, 'telefono') || '',
      prioridad: col(activo, 'prioridad') || 'ninguna',
      servicio_id: col(activo, 'servicio_id'),
      servicio_nombre,
      hora_llamado: col(activo, 'hora_llamado'),
      hora_inicio_atencion: col(activo, 'hora_inicio_atencion')
    }
  };
}

async function obtenerCola(app, query) {
  const { sede_id } = query || {};
  if (!sede_id) throw { status: 400, message: 'sede_id es requerido' };

  const datastore = app.datastore();
  const tablaTurnos = datastore.table('Turno');
  const tablaServicios = datastore.table('Servicio');
  const fecha = getFechaHoy();

  const [turnosAll, servicios] = await Promise.all([
    getAllRows(tablaTurnos),
    getAllRows(tablaServicios)
  ]);

  const turnosHoy = turnosAll.filter(t =>
    String(col(t, 'sede_id')) === String(sede_id) &&
    col(t, 'fecha_turno') === fecha
  );

  const cola = servicios.filter(s => col(s, 'activo') !== false).map(s => {
    const enCola = turnosHoy.filter(t =>
      String(col(t, 'servicio_id')) === String(s.ROWID) &&
      col(t, 'estado') === 'espera'
    );
    return {
      servicio_id: s.ROWID,
      servicio_nombre: col(s, 'nombre'),
      cantidad: enCola.length
    };
  });

  return { cola };
}

module.exports = {
  generarTurno, llamarSiguiente, rellamarTurno, iniciarAtencion,
  finalizarTurno, noSePresento, obtenerMiTurno, obtenerTurnoActivo, obtenerCola
};
