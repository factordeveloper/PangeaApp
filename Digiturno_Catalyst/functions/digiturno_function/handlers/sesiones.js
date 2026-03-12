'use strict';

const { getAllRows, col, getTimestampAhora } = require('../lib/utils');
const { requireAgente } = require('./auth');

async function iniciarSesion(app) {
  const usuario = await requireAgente(app);
  const datastore = app.datastore();
  const tabla = datastore.table('SesionAgente');
  const sesiones = await getAllRows(tabla);

  const activa = sesiones.find(s =>
    String(col(s, 'agente_id')) === String(usuario.perfil_id) && !col(s, 'hora_fin')
  );
  if (activa) {
    return { sesion: { ROWID: activa.ROWID }, message: 'Ya tiene una sesion activa' };
  }

  const tablaUsuarios = datastore.table('Usuario');
  await tablaUsuarios.updateRow({ ROWID: usuario.perfil_id, estado: 'activo' });

  const row = await tabla.insertRow({
    agente_id: usuario.perfil_id,
    sede_id: String(usuario.sede_id || ''),
    hora_inicio: getTimestampAhora(),
    hora_fin: '',
    turnos_atendidos: 0,
    tiempo_pausa_seg: 0
  });

  return { sesion: { ROWID: row.ROWID }, message: 'Sesion iniciada' };
}

async function finalizarSesion(app) {
  const usuario = await requireAgente(app);
  const datastore = app.datastore();
  const tabla = datastore.table('SesionAgente');
  const sesiones = await getAllRows(tabla);

  const activa = sesiones.find(s =>
    String(col(s, 'agente_id')) === String(usuario.perfil_id) && !col(s, 'hora_fin')
  );

  if (!activa) return { message: 'No tiene sesion activa' };

  const tablaUsuarios = datastore.table('Usuario');
  await tablaUsuarios.updateRow({ ROWID: usuario.perfil_id, estado: 'desconectado' });

  await tabla.updateRow({ ROWID: activa.ROWID, hora_fin: getTimestampAhora() });
  return { message: 'Sesion finalizada' };
}

module.exports = { iniciarSesion, finalizarSesion };
