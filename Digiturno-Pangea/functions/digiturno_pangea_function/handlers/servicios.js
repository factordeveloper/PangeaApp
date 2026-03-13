'use strict';

const { getAllRows, col } = require('../lib/utils');
const { requireAdmin } = require('./auth');

async function listarServicios(app) {
  const datastore = app.datastore();
  const tabla = datastore.table('Servicio');
  const all = await getAllRows(tabla);
  const servicios = all.map(s => ({
    ROWID: s.ROWID,
    nombre: col(s, 'nombre'),
    activo: col(s, 'activo') !== false
  }));
  return { servicios };
}

async function crearServicio(app, body) {
  await requireAdmin(app);
  const { nombre } = body || {};
  if (!nombre) throw { status: 400, message: 'nombre es requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Servicio');
  const row = await tabla.insertRow({
    nombre: String(nombre).trim(),
    activo: true
  });
  return { servicio: { ROWID: row.ROWID, nombre: row.nombre, activo: true } };
}

async function actualizarServicio(app, body, id) {
  await requireAdmin(app);
  if (!id) throw { status: 400, message: 'ID requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Servicio');
  const updates = { ROWID: id };
  if (body.nombre !== undefined) updates.nombre = String(body.nombre).trim();
  if (body.activo !== undefined) updates.activo = Boolean(body.activo);

  await tabla.updateRow(updates);
  return { servicio: updates };
}

async function eliminarServicio(app, id) {
  await requireAdmin(app);
  if (!id) throw { status: 400, message: 'ID requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Servicio');
  await tabla.updateRow({ ROWID: id, activo: false });
  return { message: 'Servicio desactivado' };
}

module.exports = { listarServicios, crearServicio, actualizarServicio, eliminarServicio };
