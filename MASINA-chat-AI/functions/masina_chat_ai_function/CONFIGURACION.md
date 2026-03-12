# Configuración del servidor MASINA Chat AI

## Parámetros del modelo (index.js)

Edita las constantes al inicio de `index.js` para personalizar el comportamiento:

| Constante | Descripción | Valor por defecto |
|-----------|-------------|-------------------|
| `DEFAULT_MODEL` | Modelo QuickML | `crm-di-qwen_text_14b-fp8-it` |
| `DEFAULT_SYSTEM_PROMPT` | Instrucciones del sistema | `Eres MASINA, asistente virtual de Grupo Masin...` |
| `DEFAULT_TOP_P` | Nucleus sampling | `0.9` |
| `DEFAULT_TOP_K` | Top-K sampling | `50` |
| `DEFAULT_BEST_OF` | Número de muestras | `1` |
| `DEFAULT_TEMPERATURE` | Creatividad (0-1) | `0.7` |
| `DEFAULT_MAX_TOKENS` | Tokens máximos de respuesta | `256` |

---

## Variables de entorno para gestión de tokens

El servidor gestiona automáticamente los access tokens a partir del `refresh_token`. Configura **una** de estas opciones en la consola de Catalyst:

### Opción 1: Variables individuales (recomendado)

En **Catalyst Console** > **Project Settings** > **Environment Variables**, añade:

| Variable | Descripción |
|----------|-------------|
| `ZOHO_REFRESH_TOKEN` | Tu refresh token de Zoho (no caduca) |
| `ZOHO_CLIENT_ID` | Client ID de tu aplicación Zoho |
| `ZOHO_CLIENT_SECRET` | Client Secret de tu aplicación Zoho |

### Opción 2: Variable CATALYST_AUTH (JSON)

Añade una sola variable `CATALYST_AUTH` con este JSON:

```json
{
  "refresh_token": "TU_REFRESH_TOKEN",
  "client_id": "TU_CLIENT_ID",
  "client_secret": "TU_CLIENT_SECRET"
}
```

### Opción 3: Client Credentials de la consola

Si ya tienes configuradas las Client Credentials en la consola de Catalyst, el servidor las usará automáticamente sin configuración adicional.

---

## Cómo obtener el refresh_token

1. Crea una aplicación en [Zoho API Console](https://api-console.zoho.com/)
2. Genera un código de autorización (Authorization Code)
3. Intercámbialo por un refresh_token con:

```bash
curl -X POST "https://accounts.zoho.com/oauth/v2/token" \
  -d "code=TU_CODIGO_AUTORIZACION" \
  -d "client_id=TU_CLIENT_ID" \
  -d "client_secret=TU_CLIENT_SECRET" \
  -d "redirect_uri=TU_REDIRECT_URI" \
  -d "grant_type=authorization_code"
```

4. La respuesta incluirá `refresh_token` (no caduca) y `access_token` (caduca en ~59 min)

---

## Gestión automática de tokens

- El servidor usa el `refresh_token` para obtener `access_token` cuando sea necesario
- Los access tokens duran ~59 minutos; el servidor los renueva automáticamente
- No necesitas enviar ningún token en las peticiones al servidor
