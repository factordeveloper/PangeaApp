'use strict';

function parseBody(req) {
  return new Promise((resolve, reject) => {
    if (req.method !== 'POST' && req.method !== 'PUT' && req.method !== 'PATCH') {
      return resolve(null);
    }
    let body = '';
    req.on('data', chunk => { body += chunk.toString(); });
    req.on('end', () => {
      try { resolve(body ? JSON.parse(body) : {}); }
      catch (e) { reject(new Error('Invalid JSON body')); }
    });
    req.on('error', reject);
  });
}

function sendJSON(res, statusCode, data) {
  const body = JSON.stringify(data);
  res.writeHead(statusCode, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization',
    'Access-Control-Allow-Credentials': 'true',
    'Content-Length': Buffer.byteLength(body)
  });
  res.end(body);
}

function sendCSV(res, csvContent, filename) {
  const buf = Buffer.from(csvContent, 'utf-8');
  res.writeHead(200, {
    'Content-Type': 'text/csv; charset=utf-8',
    'Content-Disposition': `attachment; filename="${filename}"`,
    'Access-Control-Allow-Origin': '*',
    'Content-Length': buf.length
  });
  res.end(buf);
}

function getPath(url) {
  const path = (url || '/').split('?')[0];
  return path.endsWith('/') && path.length > 1 ? path.slice(0, -1) : path;
}

function getQueryParams(url) {
  const qs = (url || '').split('?')[1];
  if (!qs) return {};
  return qs.split('&').reduce((acc, pair) => {
    const idx = pair.indexOf('=');
    const k = idx > 0 ? decodeURIComponent(pair.slice(0, idx)) : pair;
    const v = idx > 0 ? decodeURIComponent(pair.slice(idx + 1)) : '';
    if (k) acc[k] = v;
    return acc;
  }, {});
}

function getFechaHoy() {
  return new Date().toISOString().slice(0, 10);
}

function getHoraAhora() {
  return new Date().toISOString().slice(11, 19);
}

function getTimestampAhora() {
  return new Date().toISOString();
}

async function getAllRows(table) {
  const MAX = 300;
  let allData = [];
  let nextToken;
  do {
    const opts = { maxRows: MAX };
    if (nextToken) opts.nextToken = nextToken;
    const page = await table.getPagedRows(opts);
    allData = allData.concat(page.data || []);
    nextToken = page.next_token || page.nextToken;
  } while (nextToken);
  return allData;
}

function calcularSegundos(horaInicio, horaFin) {
  if (!horaInicio || !horaFin) return 0;
  const parse = (t) => {
    const p = t.split(':').map(Number);
    return p[0] * 3600 + p[1] * 60 + (p[2] || 0);
  };
  return Math.max(0, parse(horaFin) - parse(horaInicio));
}

function col(row, name) {
  if (!row) return null;
  if (row[name] !== undefined && row[name] !== null) return row[name];
  const cap = name.charAt(0).toUpperCase() + name.slice(1);
  if (row[cap] !== undefined && row[cap] !== null) return row[cap];
  return null;
}

module.exports = {
  parseBody, sendJSON, sendCSV, getPath, getQueryParams,
  getFechaHoy, getHoraAhora, getTimestampAhora,
  getAllRows, calcularSegundos, col
};
