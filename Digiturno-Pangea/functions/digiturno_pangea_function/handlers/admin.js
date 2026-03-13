'use strict';

const { getFechaHoy, getAllRows, col } = require('../lib/utils');
const { requireAdmin } = require('./auth');

async function getDashboard(app, query) {
  await requireAdmin(app);
  const sede_id = query?.sede_id;
  const datastore = app.datastore();
  const fecha = getFechaHoy();

  const [turnos, servicios, usuarios] = await Promise.all([
    getAllRows(datastore.table('Turno')),
    getAllRows(datastore.table('Servicio')),
    getAllRows(datastore.table('Usuario'))
  ]);

  let turnosHoy = turnos.filter(t => col(t, 'fecha_turno') === fecha);
  if (sede_id) {
    turnosHoy = turnosHoy.filter(t => String(col(t, 'sede_id')) === String(sede_id));
  }

  const agentesActivos = usuarios.filter(u =>
    col(u, 'rol') === 'agente' &&
    col(u, 'activo') !== false &&
    col(u, 'estado') === 'activo' &&
    (!sede_id || String(col(u, 'sede_id')) === String(sede_id))
  );

  const porServicio = servicios.filter(s => col(s, 'activo') !== false).map(s => {
    const del = turnosHoy.filter(t => String(col(t, 'servicio_id')) === String(s.ROWID));
    return {
      servicio_id: s.ROWID,
      nombre: col(s, 'nombre'),
      en_espera: del.filter(t => col(t, 'estado') === 'espera').length,
      llamados: del.filter(t => col(t, 'estado') === 'llamado').length,
      en_atencion: del.filter(t => col(t, 'estado') === 'en_atencion').length,
      finalizados: del.filter(t => col(t, 'estado') === 'finalizado').length,
      no_se_presento: del.filter(t => col(t, 'estado') === 'no_se_presento').length,
      total: del.length
    };
  });

  const finalizadosHoy = turnosHoy.filter(t => col(t, 'estado') === 'finalizado');
  const tiemposEspera = finalizadosHoy.map(t => Number(col(t, 'tiempo_espera_seg')) || 0).filter(v => v > 0);
  const tiemposAtencion = finalizadosHoy.map(t => Number(col(t, 'tiempo_atencion_seg')) || 0).filter(v => v > 0);

  const avg = arr => arr.length > 0 ? Math.round(arr.reduce((a, b) => a + b, 0) / arr.length) : 0;

  return {
    fecha,
    resumen: {
      total_turnos: turnosHoy.length,
      en_espera: turnosHoy.filter(t => col(t, 'estado') === 'espera').length,
      llamados: turnosHoy.filter(t => col(t, 'estado') === 'llamado').length,
      en_atencion: turnosHoy.filter(t => col(t, 'estado') === 'en_atencion').length,
      finalizados: finalizadosHoy.length,
      no_se_presento: turnosHoy.filter(t => col(t, 'estado') === 'no_se_presento').length,
      agentes_activos: agentesActivos.length,
      promedio_espera_seg: avg(tiemposEspera),
      promedio_atencion_seg: avg(tiemposAtencion)
    },
    por_servicio: porServicio,
    agentes: agentesActivos.map(a => ({
      ROWID: a.ROWID,
      nombre: col(a, 'nombre'),
      modulo_atencion: col(a, 'modulo_atencion'),
      estado: col(a, 'estado')
    }))
  };
}

async function getReportes(app, query) {
  await requireAdmin(app);
  const { fecha_inicio, fecha_fin, servicio_id, agente_id, sede_id } = query || {};
  const fi = fecha_inicio || getFechaHoy();
  const ff = fecha_fin || getFechaHoy();

  const datastore = app.datastore();
  const [turnos, servicios, usuarios, sedes] = await Promise.all([
    getAllRows(datastore.table('Turno')),
    getAllRows(datastore.table('Servicio')),
    getAllRows(datastore.table('Usuario')),
    getAllRows(datastore.table('Sede'))
  ]);

  let filtrados = turnos.filter(t => {
    const f = col(t, 'fecha_turno');
    return f >= fi && f <= ff;
  });

  if (sede_id) filtrados = filtrados.filter(t => String(col(t, 'sede_id')) === String(sede_id));
  if (servicio_id) filtrados = filtrados.filter(t => String(col(t, 'servicio_id')) === String(servicio_id));
  if (agente_id) filtrados = filtrados.filter(t => String(col(t, 'agente_id')) === String(agente_id));

  const sm = {}; servicios.forEach(s => { sm[String(s.ROWID)] = col(s, 'nombre'); });
  const um = {}; usuarios.forEach(u => { um[String(u.ROWID)] = col(u, 'nombre'); });
  const sdm = {}; sedes.forEach(s => { sdm[String(s.ROWID)] = col(s, 'nombre'); });

  const reportes = filtrados.map(t => ({
    ROWID: t.ROWID,
    fecha_turno: col(t, 'fecha_turno'),
    numero_turno: col(t, 'numero_turno'),
    sede: sdm[String(col(t, 'sede_id'))] || '',
    servicio: sm[String(col(t, 'servicio_id'))] || '',
    agente: um[String(col(t, 'agente_id'))] || '',
    estado: col(t, 'estado'),
    prioridad: col(t, 'prioridad') || 'ninguna',
    nombre_cliente: col(t, 'nombre_cliente') || '',
    hora_generado: col(t, 'hora_generado'),
    hora_llamado: col(t, 'hora_llamado'),
    hora_inicio_atencion: col(t, 'hora_inicio_atencion'),
    hora_fin_atencion: col(t, 'hora_fin_atencion'),
    tiempo_espera_seg: Number(col(t, 'tiempo_espera_seg')) || 0,
    tiempo_atencion_seg: Number(col(t, 'tiempo_atencion_seg')) || 0,
    llamadas: Number(col(t, 'llamadas')) || 0
  })).sort((a, b) => {
    if (a.fecha_turno !== b.fecha_turno) return a.fecha_turno.localeCompare(b.fecha_turno);
    return (a.numero_turno || '').localeCompare(b.numero_turno || '');
  });

  return { reportes, total: reportes.length };
}

async function getReportesCSV(app, query) {
  const { reportes } = await getReportes(app, query);
  const headers = ['Fecha','Turno','Sede','Servicio','Agente','Estado','Prioridad','Cliente','Hora Generado','Hora Llamado','Hora Inicio','Hora Fin','Espera (seg)','Atencion (seg)','Llamadas'];
  const rows = reportes.map(r => [
    r.fecha_turno, r.numero_turno, r.sede, r.servicio, r.agente, r.estado,
    r.prioridad, r.nombre_cliente, r.hora_generado, r.hora_llamado,
    r.hora_inicio_atencion, r.hora_fin_atencion, r.tiempo_espera_seg,
    r.tiempo_atencion_seg, r.llamadas
  ]);
  const csv = [headers.join(','), ...rows.map(r => r.map(v => `"${String(v || '').replace(/"/g, '""')}"`).join(','))].join('\n');
  return { csv, filename: `reporte_turnos_${query?.fecha_inicio || getFechaHoy()}.csv` };
}

async function resetearNumeracion(app, body) {
  await requireAdmin(app);
  return { message: 'La numeracion se reinicia automaticamente cada dia a las 00:00. Los numeros secuenciales se calculan por sede y dia.' };
}

module.exports = { getDashboard, getReportes, getReportesCSV, resetearNumeracion };
