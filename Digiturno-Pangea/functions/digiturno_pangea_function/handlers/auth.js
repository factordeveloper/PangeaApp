'use strict';

const { getAllRows, col } = require('../lib/utils');

async function getUsuarioActual(app) {
  try {
    const userManagement = app.userManagement();
    const catalystUser = await userManagement.getCurrentUser();
    if (!catalystUser || !catalystUser.user_id) {
      throw { status: 401, message: 'No autenticado' };
    }

    const datastore = app.datastore();
    const tablaUsuarios = datastore.table('Usuario');
    const usuarios = await getAllRows(tablaUsuarios);
    const usuario = usuarios.find(u =>
      String(col(u, 'catalyst_user_id')) === String(catalystUser.user_id)
    );

    if (!usuario) {
      return {
        catalyst_user_id: catalystUser.user_id,
        email: catalystUser.email_id,
        nombre: `${catalystUser.first_name || ''} ${catalystUser.last_name || ''}`.trim() || catalystUser.email_id,
        perfil_id: null,
        rol: null,
        sede_id: null,
        modulo_atencion: null,
        estado: null,
        activo: false,
        servicios_asignados: []
      };
    }

    let servicios_asignados = [];
    try {
      const tablaAS = datastore.table('AgenteServicio');
      const asignaciones = await getAllRows(tablaAS);
      servicios_asignados = asignaciones
        .filter(a => String(col(a, 'agente_id')) === String(usuario.ROWID))
        .map(a => String(col(a, 'servicio_id')));
    } catch (e) { /* tabla no existe aún */ }

    return {
      catalyst_user_id: catalystUser.user_id,
      email: catalystUser.email_id || col(usuario, 'email'),
      nombre: col(usuario, 'nombre') || `${catalystUser.first_name || ''} ${catalystUser.last_name || ''}`.trim(),
      perfil_id: usuario.ROWID,
      rol: col(usuario, 'rol') || 'agente',
      sede_id: col(usuario, 'sede_id'),
      modulo_atencion: col(usuario, 'modulo_atencion') || '1',
      estado: col(usuario, 'estado') || 'activo',
      activo: col(usuario, 'activo') !== false,
      servicios_asignados
    };
  } catch (e) {
    if (e.status) throw e;
    throw { status: 401, message: 'No autenticado' };
  }
}

async function verificarUsuario(app) {
  const usuario = await getUsuarioActual(app);
  return { valido: true, usuario };
}

async function requireAuth(app) {
  const usuario = await getUsuarioActual(app);
  if (!usuario.perfil_id) {
    throw { status: 403, message: 'No tiene perfil asignado. Contacte al administrador.' };
  }
  if (!usuario.activo) {
    throw { status: 403, message: 'Usuario desactivado.' };
  }
  return usuario;
}

async function requireAdmin(app) {
  const usuario = await requireAuth(app);
  if (usuario.rol !== 'admin') {
    throw { status: 403, message: 'Acceso solo para administradores.' };
  }
  return usuario;
}

async function requireAgente(app) {
  const usuario = await requireAuth(app);
  if (usuario.rol !== 'agente' && usuario.rol !== 'admin') {
    throw { status: 403, message: 'Acceso solo para agentes o administradores.' };
  }
  return usuario;
}

module.exports = {
  getUsuarioActual, verificarUsuario, requireAuth, requireAdmin, requireAgente
};
