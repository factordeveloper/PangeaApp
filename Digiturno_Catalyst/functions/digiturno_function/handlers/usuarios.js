'use strict';

const { getAllRows, col } = require('../lib/utils');
const { requireAdmin } = require('./auth');

async function listarUsuarios(app) {
  await requireAdmin(app);
  const datastore = app.datastore();
  const tablaUsuarios = datastore.table('Usuario');
  const usuarios = await getAllRows(tablaUsuarios);

  let asignaciones = [];
  try {
    const tablaAS = datastore.table('AgenteServicio');
    asignaciones = await getAllRows(tablaAS);
  } catch (e) { /* */ }

  const resultado = usuarios.map(u => {
    const servicios = asignaciones
      .filter(a => String(col(a, 'agente_id')) === String(u.ROWID))
      .map(a => String(col(a, 'servicio_id')));

    return {
      ROWID: u.ROWID,
      catalyst_user_id: col(u, 'catalyst_user_id'),
      nombre: col(u, 'nombre'),
      email: col(u, 'email'),
      sede_id: col(u, 'sede_id'),
      rol: col(u, 'rol') || 'agente',
      modulo_atencion: col(u, 'modulo_atencion'),
      estado: col(u, 'estado') || 'desconectado',
      activo: col(u, 'activo') !== false,
      servicios_asignados: servicios
    };
  });

  return { usuarios: resultado };
}

async function crearUsuario(app, body) {
  await requireAdmin(app);
  const { catalyst_user_id, nombre, email, sede_id, rol = 'agente', modulo_atencion = '1' } = body || {};

  if (!catalyst_user_id) throw { status: 400, message: 'catalyst_user_id es requerido' };
  if (!nombre) throw { status: 400, message: 'nombre es requerido' };
  if (!['admin', 'agente'].includes(rol)) throw { status: 400, message: 'Rol debe ser admin o agente' };

  const datastore = app.datastore();
  const tablaUsuarios = datastore.table('Usuario');
  const existentes = await getAllRows(tablaUsuarios);
  const existe = existentes.find(u => String(col(u, 'catalyst_user_id')) === String(catalyst_user_id));
  if (existe) throw { status: 400, message: 'Ya existe un perfil para este usuario' };

  const row = await tablaUsuarios.insertRow({
    catalyst_user_id: String(catalyst_user_id),
    nombre: String(nombre).trim(),
    email: String(email || '').trim(),
    sede_id: sede_id ? String(sede_id) : '',
    rol,
    modulo_atencion: String(modulo_atencion),
    estado: 'desconectado',
    activo: true
  });

  return { usuario: { ROWID: row.ROWID, nombre: row.nombre, rol: row.rol } };
}

async function actualizarUsuario(app, body, id) {
  await requireAdmin(app);
  if (!id) throw { status: 400, message: 'ID requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Usuario');
  const updates = { ROWID: id };

  if (body.nombre !== undefined) updates.nombre = String(body.nombre).trim();
  if (body.email !== undefined) updates.email = String(body.email).trim();
  if (body.sede_id !== undefined) updates.sede_id = String(body.sede_id);
  if (body.rol !== undefined) updates.rol = body.rol;
  if (body.modulo_atencion !== undefined) updates.modulo_atencion = String(body.modulo_atencion);
  if (body.estado !== undefined) updates.estado = body.estado;
  if (body.activo !== undefined) updates.activo = Boolean(body.activo);

  await tabla.updateRow(updates);
  return { usuario: updates };
}

async function eliminarUsuario(app, id) {
  await requireAdmin(app);
  if (!id) throw { status: 400, message: 'ID requerido' };

  const datastore = app.datastore();
  const tabla = datastore.table('Usuario');
  await tabla.updateRow({ ROWID: id, activo: false });
  return { message: 'Usuario desactivado' };
}

async function asignarServicios(app, body, userId) {
  await requireAdmin(app);
  const { servicios = [] } = body || {};

  const datastore = app.datastore();
  const tablaAS = datastore.table('AgenteServicio');

  const existentes = await getAllRows(tablaAS);
  const delUsuario = existentes.filter(a => String(col(a, 'agente_id')) === String(userId));
  for (const asig of delUsuario) {
    await tablaAS.deleteRows([asig.ROWID]);
  }

  for (const servicio_id of servicios) {
    await tablaAS.insertRow({
      agente_id: Number(userId),
      servicio_id: String(servicio_id)
    });
  }

  return { message: 'Servicios asignados correctamente', servicios };
}

async function listarUsuariosCatalyst(app) {
  await requireAdmin(app);
  const userManagement = app.userManagement();
  const usuarios = await userManagement.getAllUsers();
  return {
    usuarios: (usuarios || []).map(u => ({
      user_id: u.user_id,
      email: u.email_id,
      nombre: `${u.first_name || ''} ${u.last_name || ''}`.trim() || u.email_id,
      creado: u.created_time
    }))
  };
}

module.exports = { listarUsuarios, crearUsuario, actualizarUsuario, eliminarUsuario, asignarServicios, listarUsuariosCatalyst };
