/**
 * DigiTurno v3 — Cliente API compartido
 */
const API_BASE = (() => {
  if (typeof window !== 'undefined' && window.location) {
    const o = window.location.origin;
    if (o.includes('catalystserverless.com')) return o + '/server/digiturno_function';
    return 'https://digiturno-793004668.development.catalystserverless.com/server/digiturno_function';
  }
  return '/server/digiturno_function';
})();

const AUTH_BASE = (() => {
  if (typeof window !== 'undefined' && window.location) {
    const o = window.location.origin;
    if (o.includes('catalystserverless.com')) return o;
  }
  return 'https://digiturno-793004668.development.catalystserverless.com';
})();

async function api(method, path, body) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include'
  };
  if (body && ['POST', 'PUT', 'PATCH'].includes(method)) {
    opts.body = JSON.stringify(body);
  }
  const res = await fetch(API_BASE + path, opts);
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    if (res.status === 401 && !path.includes('/auth/verificar')) {
      sessionStorage.setItem('digiturno_redirect', window.location.href);
      window.location.href = AUTH_BASE + '/__catalyst/auth/login';
    }
    throw new Error(data.error || `Error ${res.status}`);
  }
  return data;
}

const DigiturnoAPI = {
  redirectToLogin: () => {
    sessionStorage.setItem('digiturno_redirect', window.location.href);
    window.location.href = AUTH_BASE + '/__catalyst/auth/login';
  },
  redirectToSignup: () => {
    sessionStorage.setItem('digiturno_redirect', window.location.href);
    window.location.href = AUTH_BASE + '/__catalyst/auth/signup';
  },
  redirectToResetPassword: () => {
    window.location.href = AUTH_BASE + '/__catalyst/auth/reset-password';
  },
  handlePostLoginRedirect: () => {
    const dest = sessionStorage.getItem('digiturno_redirect');
    if (dest) {
      sessionStorage.removeItem('digiturno_redirect');
      const currentUrl = window.location.href.split('?')[0].split('#')[0];
      const destClean = dest.split('?')[0].split('#')[0];
      if (destClean !== currentUrl) {
        window.location.href = dest;
        return true;
      }
    }
    return false;
  },
  logout: () => {
    localStorage.removeItem('digiturno_usuario');
    sessionStorage.removeItem('digiturno_redirect');
    if (typeof catalyst !== 'undefined' && catalyst.auth) {
      catalyst.auth.signOut(AUTH_BASE + '/app/index.html');
      return;
    }
    window.location.href = AUTH_BASE + '/__catalyst/auth/logout';
  },
  verificarSesion: async () => {
    const r = await api('GET', '/api/auth/verificar');
    if (r.usuario) localStorage.setItem('digiturno_usuario', JSON.stringify(r.usuario));
    return r;
  },

  listarSedes: () => api('GET', '/api/sedes'),
  crearSede: d => api('POST', '/api/sedes', d),
  actualizarSede: (id, d) => api('PUT', `/api/sedes/${id}`, d),
  eliminarSede: id => api('DELETE', `/api/sedes/${id}`),
  buscarSede: nombre => api('GET', `/api/sedes/buscar?nombre=${encodeURIComponent(nombre)}`),

  listarServicios: () => api('GET', '/api/servicios'),
  crearServicio: d => api('POST', '/api/servicios', d),
  actualizarServicio: (id, d) => api('PUT', `/api/servicios/${id}`, d),
  eliminarServicio: id => api('DELETE', `/api/servicios/${id}`),

  listarUsuarios: () => api('GET', '/api/usuarios'),
  crearUsuario: d => api('POST', '/api/usuarios', d),
  actualizarUsuario: (id, d) => api('PUT', `/api/usuarios/${id}`, d),
  eliminarUsuario: id => api('DELETE', `/api/usuarios/${id}`),
  asignarServicios: (uid, s) => api('POST', `/api/usuarios/${uid}/servicios`, { servicios: s }),
  listarUsuariosCatalyst: () => api('GET', '/api/usuarios/catalyst'),

  generarTurno: d => api('POST', '/api/turnos/generar', d),
  llamarSiguiente: servicio_id => api('POST', '/api/turnos/llamar', { servicio_id }),
  rellamarTurno: turno_id => api('POST', '/api/turnos/rellamar', { turno_id }),
  iniciarAtencion: turno_id => api('POST', '/api/turnos/atender', { turno_id }),
  finalizarTurno: turno_id => api('POST', '/api/turnos/finalizar', { turno_id }),
  noSePresento: turno_id => api('POST', '/api/turnos/no-se-presento', { turno_id }),
  obtenerMiTurno: turno_id => api('GET', `/api/turnos/mi-turno?turno_id=${turno_id}`),
  obtenerTurnoActivo: () => api('GET', '/api/turnos/activo'),
  obtenerCola: sede_id => api('GET', `/api/turnos/cola?sede_id=${sede_id}`),

  getEstadoPantalla: sede_id => api('GET', `/api/pantalla/estado?sede_id=${sede_id}`),

  getDashboard: sede_id => api('GET', sede_id ? `/api/admin/dashboard?sede_id=${sede_id}` : '/api/admin/dashboard'),
  getReportes: p => { const qs = new URLSearchParams(p).toString(); return api('GET', `/api/admin/reportes?${qs}`); },
  getReportesCSV: async (p) => {
    const qs = new URLSearchParams(p).toString();
    const res = await fetch(API_BASE + `/api/admin/reportes/csv?${qs}`, { credentials: 'include' });
    if (!res.ok) throw new Error('Error al exportar CSV');
    return res.text();
  },
  resetearNumeracion: () => api('POST', '/api/admin/reset'),

  iniciarSesionAgente: () => api('POST', '/api/sesion/iniciar'),
  finalizarSesionAgente: () => api('POST', '/api/sesion/finalizar')
};

function formatTime(s) {
  if (!s || s <= 0) return '0s';
  const m = Math.floor(s / 60);
  const sec = s % 60;
  return m > 0 ? `${m}m ${sec}s` : `${sec}s`;
}

function downloadCSV(csv, filename) {
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = filename;
  a.click();
  URL.revokeObjectURL(a.href);
}
