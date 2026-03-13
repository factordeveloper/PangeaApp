'use strict';

const { getAllRows, col } = require('../lib/utils');
const { requireAdmin } = require('./auth');

async function listarSedes(app) {
  const datastore = app.datastore();
  const tabla = datastore.table('Sede');
  const all = await getAllRows(tabla);
  const sedes = all.map(s => ({
    ROWID: s.ROWID,
    nombre: col(s, 'nombre'),
    direccion: col(s, 'direccion'),
    activo: col(s, 'activo') !== false
  }));
  return { sedes };
}

async function crearSede(app, body) {
  await requireAdmin(app);
  const { nombre, direccion = '' } = body || {};
  if (!nombre) throw { status: 400, message: 'nombre es requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Sede');
  const row = await tabla.insertRow({
    nombre: String(nombre).trim(),
    direccion: String(direccion).trim(),
    activo: true
  });
  return { sede: { ROWID: row.ROWID, nombre: row.nombre, direccion: row.direccion, activo: true } };
}

async function actualizarSede(app, body, id) {
  await requireAdmin(app);
  if (!id) throw { status: 400, message: 'ID requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Sede');
  const updates = { ROWID: id };
  if (body.nombre !== undefined) updates.nombre = String(body.nombre).trim();
  if (body.direccion !== undefined) updates.direccion = String(body.direccion).trim();
  if (body.activo !== undefined) updates.activo = Boolean(body.activo);

  await tabla.updateRow(updates);
  return { sede: updates };
}

async function eliminarSede(app, id) {
  await requireAdmin(app);
  if (!id) throw { status: 400, message: 'ID requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Sede');
  await tabla.updateRow({ ROWID: id, activo: false });
  return { message: 'Sede desactivada' };
}

async function buscarSedePorNombre(app, nombre) {
  if (!nombre) throw { status: 400, message: 'nombre es requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Sede');
  const all = await getAllRows(tabla);
  const normalizado = String(nombre).toLowerCase().replace(/[-_\s]+/g, '');
  const sede = all.find(s => {
    const n = String(col(s, 'nombre') || '').toLowerCase().replace(/[-_\s]+/g, '');
    return n === normalizado && col(s, 'activo') !== false;
  });

  if (!sede) throw { status: 404, message: 'Sede no encontrada' };
  return {
    sede: {
      ROWID: sede.ROWID,
      nombre: col(sede, 'nombre'),
      direccion: col(sede, 'direccion')
    }
  };
}

module.exports = { listarSedes, crearSede, actualizarSede, eliminarSede, buscarSedePorNombre };
