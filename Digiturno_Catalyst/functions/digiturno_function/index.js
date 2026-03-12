'use strict';

const catalyst = require('zcatalyst-sdk-node');
const { parseBody, sendJSON, sendCSV, getPath, getQueryParams } = require('./lib/utils');
const { verificarUsuario } = require('./handlers/auth');
const { listarSedes, crearSede, actualizarSede, eliminarSede, buscarSedePorNombre } = require('./handlers/sedes');
const { listarServicios, crearServicio, actualizarServicio, eliminarServicio } = require('./handlers/servicios');
const { listarUsuarios, crearUsuario, actualizarUsuario, eliminarUsuario, asignarServicios, listarUsuariosCatalyst } = require('./handlers/usuarios');
const { generarTurno, llamarSiguiente, rellamarTurno, iniciarAtencion, finalizarTurno, noSePresento, obtenerMiTurno, obtenerTurnoActivo, obtenerCola } = require('./handlers/turnos');
const { getEstadoPantalla } = require('./handlers/pantalla');
const { getDashboard, getReportes, getReportesCSV, resetearNumeracion } = require('./handlers/admin');
const { iniciarSesion, finalizarSesion } = require('./handlers/sesiones');

const BASE = '/server/digiturno_function';

function normalizePath(url) {
  let path = getPath(url);
  if (!path.startsWith('/')) path = '/' + path;
  if (path.startsWith(BASE)) path = path.slice(BASE.length) || '/';
  if (path.endsWith('/') && path.length > 1) path = path.slice(0, -1);
  return path;
}

async function handleRequest(req, res, app, path, method, body, query) {
  try {
    let result;

    // ===== AUTH =====
    if (path === '/api/auth/verificar' && method === 'GET') {
      result = await verificarUsuario(app);
    }
    // ===== SEDES =====
    else if (path === '/api/sedes' && method === 'GET') {
      result = await listarSedes(app);
    }
    else if (path === '/api/sedes' && method === 'POST') {
      result = await crearSede(app, body);
    }
    else if (path === '/api/sedes/buscar' && method === 'GET') {
      result = await buscarSedePorNombre(app, query.nombre);
    }
    else if (path.match(/^\/api\/sedes\/\d+$/) && method === 'PUT') {
      result = await actualizarSede(app, body, path.split('/').pop());
    }
    else if (path.match(/^\/api\/sedes\/\d+$/) && method === 'DELETE') {
      result = await eliminarSede(app, path.split('/').pop());
    }
    // ===== SERVICIOS =====
    else if (path === '/api/servicios' && method === 'GET') {
      result = await listarServicios(app);
    }
    else if (path === '/api/servicios' && method === 'POST') {
      result = await crearServicio(app, body);
    }
    else if (path.match(/^\/api\/servicios\/\d+$/) && method === 'PUT') {
      result = await actualizarServicio(app, body, path.split('/').pop());
    }
    else if (path.match(/^\/api\/servicios\/\d+$/) && method === 'DELETE') {
      result = await eliminarServicio(app, path.split('/').pop());
    }
    // ===== USUARIOS =====
    else if (path === '/api/usuarios' && method === 'GET') {
      result = await listarUsuarios(app);
    }
    else if (path === '/api/usuarios' && method === 'POST') {
      result = await crearUsuario(app, body);
    }
    else if (path === '/api/usuarios/catalyst' && method === 'GET') {
      result = await listarUsuariosCatalyst(app);
    }
    else if (path.match(/^\/api\/usuarios\/\d+$/) && method === 'PUT') {
      result = await actualizarUsuario(app, body, path.split('/').pop());
    }
    else if (path.match(/^\/api\/usuarios\/\d+$/) && method === 'DELETE') {
      result = await eliminarUsuario(app, path.split('/').pop());
    }
    else if (path.match(/^\/api\/usuarios\/\d+\/servicios$/) && method === 'POST') {
      result = await asignarServicios(app, body, path.split('/')[3]);
    }
    // ===== TURNOS =====
    else if (path === '/api/turnos/generar' && method === 'POST') {
      result = await generarTurno(app, body);
    }
    else if (path === '/api/turnos/llamar' && method === 'POST') {
      result = await llamarSiguiente(app, body);
    }
    else if (path === '/api/turnos/rellamar' && method === 'POST') {
      result = await rellamarTurno(app, body);
    }
    else if (path === '/api/turnos/atender' && method === 'POST') {
      result = await iniciarAtencion(app, body);
    }
    else if (path === '/api/turnos/finalizar' && method === 'POST') {
      result = await finalizarTurno(app, body);
    }
    else if (path === '/api/turnos/no-se-presento' && method === 'POST') {
      result = await noSePresento(app, body);
    }
    else if (path === '/api/turnos/mi-turno' && method === 'GET') {
      result = await obtenerMiTurno(app, query);
    }
    else if (path === '/api/turnos/activo' && method === 'GET') {
      result = await obtenerTurnoActivo(app);
    }
    else if (path === '/api/turnos/cola' && method === 'GET') {
      result = await obtenerCola(app, query);
    }
    // ===== PANTALLA =====
    else if (path === '/api/pantalla/estado' && method === 'GET') {
      result = await getEstadoPantalla(app, query);
    }
    // ===== ADMIN =====
    else if (path === '/api/admin/dashboard' && method === 'GET') {
      result = await getDashboard(app, query);
    }
    else if (path === '/api/admin/reportes' && method === 'GET') {
      result = await getReportes(app, query);
    }
    else if (path === '/api/admin/reportes/csv' && method === 'GET') {
      const { csv, filename } = await getReportesCSV(app, query);
      sendCSV(res, csv, filename);
      return;
    }
    else if (path === '/api/admin/reset' && method === 'POST') {
      result = await resetearNumeracion(app, body);
    }
    // ===== SESIONES =====
    else if (path === '/api/sesion/iniciar' && method === 'POST') {
      result = await iniciarSesion(app);
    }
    else if (path === '/api/sesion/finalizar' && method === 'POST') {
      result = await finalizarSesion(app);
    }
    // ===== CORS =====
    else if (method === 'OPTIONS') {
      res.writeHead(204, {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization',
        'Access-Control-Allow-Credentials': 'true',
        'Access-Control-Max-Age': '86400'
      });
      res.end();
      return;
    }
    // ===== HEALTH =====
    else if (path === '/' || path === '/api') {
      sendJSON(res, 200, { app: 'DigiTurno', version: '3.0', status: 'ok' });
      return;
    }
    else {
      sendJSON(res, 404, { error: 'Endpoint no encontrado', path });
      return;
    }

    sendJSON(res, 200, result);
  } catch (err) {
    const status = err.status || 500;
    const message = err.message || 'Error interno del servidor';
    sendJSON(res, status, { error: message });
  }
}

module.exports = async (req, res) => {
  const path = normalizePath(req.url);
  const method = req.method || 'GET';
  const query = getQueryParams(req.url);

  let body = null;
  try { body = await parseBody(req); }
  catch (e) { sendJSON(res, 400, { error: 'Body JSON invalido' }); return; }

  const app = catalyst.initialize(req);
  await handleRequest(req, res, app, path, method, body, query);
};
