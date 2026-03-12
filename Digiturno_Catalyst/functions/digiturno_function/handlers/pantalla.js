'use strict';

const { getFechaHoy, getAllRows, col } = require('../lib/utils');

async function getEstadoPantalla(app, query) {
  const sede_id = query?.sede_id;
  if (!sede_id) throw { status: 400, message: 'sede_id es requerido' };

  const datastore = app.datastore();
  const fecha = getFechaHoy();

  const [turnosAll, servicios, usuarios] = await Promise.all([
    getAllRows(datastore.table('Turno')),
    getAllRows(datastore.table('Servicio')),
    getAllRows(datastore.table('Usuario'))
  ]);

  const turnos = turnosAll.filter(t =>
    String(col(t, 'sede_id')) === String(sede_id) &&
    col(t, 'fecha_turno') === fecha
  );

  const servicioMap = {};
  servicios.forEach(s => { servicioMap[String(s.ROWID)] = col(s, 'nombre') || 'Servicio'; });

  const usuarioMap = {};
  usuarios.forEach(u => { usuarioMap[String(u.ROWID)] = col(u, 'modulo_atencion') || ''; });

  const activos = turnos
    .filter(t => ['llamado', 'en_atencion'].includes(col(t, 'estado')))
    .map(t => ({
      numero_turno: col(t, 'numero_turno'),
      estado: col(t, 'estado'),
      servicio_nombre: servicioMap[String(col(t, 'servicio_id'))] || 'Servicio',
      modulo_atencion: usuarioMap[String(col(t, 'agente_id'))] || '',
      llamadas: Number(col(t, 'llamadas')) || 1,
      hora_llamado: col(t, 'hora_llamado')
    }));

  const finalizados = turnos
    .filter(t => col(t, 'estado') === 'finalizado')
    .sort((a, b) => {
      const ha = col(a, 'hora_fin_atencion') || '';
      const hb = col(b, 'hora_fin_atencion') || '';
      return hb.localeCompare(ha);
    })
    .slice(0, 5)
    .map(t => ({
      numero_turno: col(t, 'numero_turno'),
      modulo_atencion: usuarioMap[String(col(t, 'agente_id'))] || ''
    }));

  const cola = servicios
    .filter(s => col(s, 'activo') !== false)
    .map(s => ({
      servicio_nombre: col(s, 'nombre'),
      en_espera: turnos.filter(t =>
        String(col(t, 'servicio_id')) === String(s.ROWID) && col(t, 'estado') === 'espera'
      ).length
    }))
    .filter(s => s.en_espera > 0);

  return { activos, finalizados, cola, fecha, actualizado: new Date().toISOString() };
}

module.exports = { getEstadoPantalla };
