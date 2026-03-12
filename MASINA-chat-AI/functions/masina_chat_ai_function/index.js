'use strict';

const { IncomingMessage, ServerResponse } = require("http");
const catalyst = require("zcatalyst-sdk-node");

// Configuración de la API QuickML
const QUICKML_API_URL = 'https://api.catalyst.zoho.com/quickml/v2/project/12018000000471172/llm/chat';
const CATALYST_ORG_ID = '793004668';

// =============================================================================
// Parámetros configurables del modelo (edita aquí para personalizar MASINA)
// =============================================================================
const DEFAULT_MODEL = 'crm-di-qwen_text_14b-fp8-it';
const DEFAULT_SYSTEM_PROMPT = '';
const DEFAULT_TOP_P = 0.9;
const DEFAULT_TOP_K = 50;
const DEFAULT_BEST_OF = 1;
const DEFAULT_TEMPERATURE = 0.7;
const DEFAULT_MAX_TOKENS = 256;

// Caché de credencial con refresh_token para reutilizar entre invocaciones (token válido ~59 min)
let refreshTokenCredential = null;

/**
 * Obtiene un access_token válido. Prioridad:
 * 1. Variables ZOHO_REFRESH_TOKEN, ZOHO_CLIENT_ID, ZOHO_CLIENT_SECRET
 * 2. Variable CATALYST_AUTH (JSON con refresh_token, client_id, client_secret)
 * 3. Credenciales por defecto de Catalyst (Client Credentials de la consola)
 * @returns {Promise<string>} access_token
 */
async function getAccessToken() {
	const refreshToken = process.env.ZOHO_REFRESH_TOKEN;
	const clientId = process.env.ZOHO_CLIENT_ID;
	const clientSecret = process.env.ZOHO_CLIENT_SECRET;

	if (refreshToken && clientId && clientSecret) {
		if (!refreshTokenCredential) {
			refreshTokenCredential = catalyst.credential.refreshToken({
				refresh_token: refreshToken,
				client_id: clientId,
				client_secret: clientSecret,
			});
		}
		const { access_token } = await refreshTokenCredential.getToken();
		return access_token;
	}

	const catalystAuth = process.env.CATALYST_AUTH;
	if (catalystAuth) {
		try {
			const auth = typeof catalystAuth === 'string' ? JSON.parse(catalystAuth) : catalystAuth;
			if (auth.refresh_token && auth.client_id && auth.client_secret) {
			if (!refreshTokenCredential) {
				refreshTokenCredential = catalyst.credential.refreshToken(auth);
			}
				const { access_token } = await refreshTokenCredential.getToken();
				return access_token;
			}
		} catch (e) {
			console.error('Error parseando CATALYST_AUTH:', e.message);
		}
	}

	const app = catalyst.initializeApp();
	const { access_token } = await app.credential.getToken();
	return access_token;
}

/**
 * Lee el body de la petición HTTP
 * @param {IncomingMessage} req
 * @returns {Promise<string>}
 */
function readBody(req) {
	return new Promise((resolve, reject) => {
		let body = '';
		req.on('data', chunk => body += chunk);
		req.on('end', () => resolve(body));
		req.on('error', reject);
	});
}

/**
 * Envía la petición a la API QuickML de Zoho
 * @param {object} payload - Datos para el chat
 * @param {string} accessToken - Token de autorización
 * @returns {Promise<object>}
 */
async function callQuickML(payload, accessToken) {
	const response = await fetch(QUICKML_API_URL, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': `Bearer ${accessToken}`,
			'CATALYST-ORG': CATALYST_ORG_ID,
		},
		body: JSON.stringify(payload),
	});

	const data = await response.json();

	if (!response.ok) {
		throw new Error(data.message || data.error || `HTTP ${response.status}`);
	}

	return data;
}

/**
 * Responde con JSON
 * @param {ServerResponse} res
 * @param {number} statusCode
 * @param {object} data
 */
function sendJson(res, statusCode, data) {
	res.writeHead(statusCode, { 'Content-Type': 'application/json' });
	res.write(JSON.stringify(data));
	res.end();
}

/**
 * @param {IncomingMessage} req
 * @param {ServerResponse} res
 */
module.exports = async (req, res) => {
	const url = req.url;
	const method = req.method;

	// CORS headers para permitir peticiones desde la app Android
	res.setHeader('Access-Control-Allow-Origin', '*');
	res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
	res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

	if (method === 'OPTIONS') {
		res.writeHead(204);
		res.end();
		return;
	}

	try {
		if (url === '/' && method === 'GET') {
			res.writeHead(200, { 'Content-Type': 'text/html' });
			res.write('<h1>MASINA Chat AI</h1><p>API activa. Usa POST / o POST /chat con {"prompt": "tu mensaje"}</p>');
			res.end();
			return;
		}

		if (url === '/' || url === '/chat') {
			if (method !== 'POST') {
				sendJson(res, 405, {
					error: 'Método no permitido',
					message: 'Usa POST para enviar mensajes al chat',
				});
				return;
			}

			const body = await readBody(req);
			let input;

			try {
				input = body ? JSON.parse(body) : {};
			} catch {
				sendJson(res, 400, {
					error: 'JSON inválido',
					message: 'El body debe ser un JSON válido',
				});
				return;
			}

			let prompt = input.prompt || input.message || '';
			const conversationHistory = input.conversation_history || input.conversationHistory || [];

			if (!prompt.trim()) {
				sendJson(res, 400, {
					error: 'Campo requerido',
					message: 'Debes enviar "prompt" o "message" con el texto del mensaje',
				});
				return;
			}

			// Si hay historial, construir prompt con contexto (últimos 6 mensajes)
			if (Array.isArray(conversationHistory) && conversationHistory.length > 0) {
				const recent = conversationHistory.slice(-6);
				const context = recent.map(m => `${m.role === 'user' ? 'Usuario' : 'Asistente'}: ${m.content}`).join('\n');
				prompt = `${context}\n\nUsuario: ${prompt.trim()}`;
			} else {
				prompt = prompt.trim();
			}

			const access_token = await getAccessToken();

			const payload = {
				prompt: prompt.trim(),
				model: input.model || DEFAULT_MODEL,
				system_prompt: input.system_prompt ?? DEFAULT_SYSTEM_PROMPT,
				top_p: input.top_p ?? DEFAULT_TOP_P,
				top_k: input.top_k ?? DEFAULT_TOP_K,
				best_of: input.best_of ?? DEFAULT_BEST_OF,
				temperature: input.temperature ?? DEFAULT_TEMPERATURE,
				max_tokens: input.max_tokens ?? DEFAULT_MAX_TOKENS,
			};

			const result = await callQuickML(payload, access_token);
			sendJson(res, 200, result);
			return;
		}

		res.writeHead(404, { 'Content-Type': 'application/json' });
		res.write(JSON.stringify({
			error: 'No encontrado',
			rutas: ['GET /', 'POST /', 'POST /chat'],
		}));
		res.end();
	} catch (err) {
		console.error('Error:', err);
		sendJson(res, 500, {
			error: 'Error interno',
			message: err.message || 'Error al procesar la petición',
		});
	}
};
