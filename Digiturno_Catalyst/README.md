# DigiTurno v3 — Sistema de Gestion Digital de Turnos

Sistema completo de gestion de turnos y filas de atencion, multi-sede, construido con **Node.js**, **Zoho Catalyst** (Functions + DataStore + Auth) y frontend estatico vanilla.

---

## Arquitectura

```
Digiturno_Catalyst/
├── catalyst.json                    # Configuracion del proyecto Catalyst
├── client/                          # Frontend estatico (servido en /app/)
│   ├── main.js                      # API client compartido
│   ├── main.css                     # Estilos globales
│   ├── index.html                   # Landing page
│   ├── cliente.html                 # Generar turno (publico)
│   ├── pantalla.html                # Pantalla publica (publico)
│   ├── agente.html                  # Panel de atencion (requiere login)
│   └── admin.html                   # Panel administrativo (requiere login admin)
└── functions/
    └── digiturno_function/          # Catalyst Function (Node.js serverless)
        ├── index.js                 # Router principal
        ├── package.json
        ├── lib/
        │   └── utils.js             # Utilidades compartidas
        └── handlers/
            ├── auth.js              # Autenticacion y autorizacion
            ├── sedes.js             # CRUD de sedes
            ├── servicios.js         # CRUD de servicios
            ├── usuarios.js          # CRUD de usuarios/agentes
            ├── turnos.js            # Logica de turnos
            ├── pantalla.js          # Estado pantalla publica
            ├── admin.js             # Dashboard y reportes
            └── sesiones.js          # Sesiones de agentes
```

---

## Tablas en Catalyst DataStore

Debes crear las siguientes 6 tablas manualmente en la consola de Catalyst:

### 1. Sede

| Columna    | Tipo    | Descripcion                    |
|------------|---------|--------------------------------|
| ROWID      | bigint  | PK auto-generada por Catalyst  |
| nombre     | text    | Nombre de la sede (unico)      |
| direccion  | text    | Direccion fisica               |
| activo     | boolean | Sede activa/inactiva           |

### 2. Servicio

| Columna    | Tipo    | Descripcion                    |
|------------|---------|--------------------------------|
| ROWID      | bigint  | PK auto-generada               |
| nombre     | text    | Nombre del servicio            |
| activo     | boolean | Servicio activo/inactivo       |

### 3. Usuario

| Columna           | Tipo    | Descripcion                           |
|-------------------|---------|---------------------------------------|
| ROWID             | bigint  | PK auto-generada                      |
| catalyst_user_id  | text    | ID del usuario en Catalyst Auth       |
| nombre            | text    | Nombre completo                       |
| email             | text    | Email                                 |
| sede_id           | text    | FK a Sede.ROWID                       |
| rol               | text    | 'admin' o 'agente'                    |
| modulo_atencion   | text    | Numero de modulo/ventanilla (ej: "1") |
| estado            | text    | 'activo', 'pausa', 'desconectado'     |
| activo            | boolean | Usuario activo/inactivo               |

### 4. Turno

| Columna              | Tipo    | Descripcion                                  |
|----------------------|---------|----------------------------------------------|
| ROWID                | bigint  | PK auto-generada                             |
| sede_id              | text    | FK a Sede.ROWID                              |
| servicio_id          | text    | FK a Servicio.ROWID                          |
| agente_id            | text    | FK a Usuario.ROWID (agente que atiende)      |
| numero_turno         | text    | Codigo visible (ej: "A-001", "D-003")        |
| llamadas             | int     | Cantidad de veces llamado (max 3)            |
| numero_secuencial    | int     | Numero secuencial por sede/dia               |
| estado               | text    | 'espera','llamado','en_atencion','finalizado','no_se_presento' |
| fecha_turno          | text    | Fecha YYYY-MM-DD                             |
| hora_generado        | text    | HH:mm:ss                                     |
| hora_llamado         | text    | HH:mm:ss (cuando el agente lo llama)         |
| hora_inicio_atencion | text    | HH:mm:ss (cuando inicia la atencion)         |
| hora_fin_atencion    | text    | HH:mm:ss (cuando finaliza)                   |
| tiempo_espera_seg    | int     | Segundos desde generacion hasta llamado      |
| tiempo_atencion_seg  | int     | Segundos de atencion                         |
| nombre_cliente       | text    | Nombre del cliente                           |
| cedula               | text    | Cedula del cliente                           |
| telefono             | text    | Telefono del cliente                         |
| prioridad            | text    | 'ninguna','discapacidad','embarazo','adulto_mayor' |

### 5. SesionAgente

| Columna          | Tipo    | Descripcion                        |
|------------------|---------|------------------------------------|
| ROWID            | bigint  | PK auto-generada                   |
| agente_id        | text    | FK a Usuario.ROWID                 |
| sede_id          | text    | FK a Sede.ROWID                    |
| hora_inicio      | text    | Timestamp ISO inicio de sesion     |
| hora_fin         | text    | Timestamp ISO fin de sesion        |
| turnos_atendidos | int     | Cantidad de turnos atendidos       |
| tiempo_pausa_seg | int     | Segundos en pausa durante sesion   |

### 6. AgenteServicio

| Columna     | Tipo    | Descripcion                    |
|-------------|---------|--------------------------------|
| ROWID       | bigint  | PK auto-generada               |
| agente_id   | text    | FK a Usuario.ROWID             |
| servicio_id | text    | FK a Servicio.ROWID            |

---

## API REST — Endpoints

### Publicos (sin autenticacion)

| Metodo | Endpoint                              | Descripcion                          |
|--------|---------------------------------------|--------------------------------------|
| GET    | /api/sedes                            | Listar sedes activas                 |
| GET    | /api/sedes/buscar?nombre=X            | Buscar sede por nombre               |
| GET    | /api/servicios                        | Listar servicios                     |
| POST   | /api/turnos/generar                   | Generar nuevo turno                  |
| GET    | /api/turnos/mi-turno?turno_id=X       | Estado de un turno (notificaciones)  |
| GET    | /api/turnos/cola?sede_id=X            | Cola por servicio en una sede        |
| GET    | /api/pantalla/estado?sede_id=X        | Estado pantalla publica              |

### Agente (requiere login, rol agente o admin)

| Metodo | Endpoint                    | Descripcion                          |
|--------|-----------------------------|--------------------------------------|
| GET    | /api/auth/verificar         | Verificar sesion                     |
| POST   | /api/turnos/llamar          | Llamar siguiente turno               |
| POST   | /api/turnos/rellamar        | Re-llamar turno actual               |
| POST   | /api/turnos/atender         | Iniciar atencion                     |
| POST   | /api/turnos/finalizar       | Finalizar turno                      |
| POST   | /api/turnos/no-se-presento  | Marcar no se presento                |
| GET    | /api/turnos/activo          | Turno activo del agente              |
| POST   | /api/sesion/iniciar         | Iniciar sesion de trabajo            |
| POST   | /api/sesion/finalizar       | Finalizar sesion de trabajo          |

### Admin (requiere login, rol admin)

| Metodo | Endpoint                           | Descripcion                     |
|--------|------------------------------------|---------------------------------|
| POST   | /api/sedes                         | Crear sede                      |
| PUT    | /api/sedes/:id                     | Actualizar sede                 |
| DELETE | /api/sedes/:id                     | Desactivar sede                 |
| POST   | /api/servicios                     | Crear servicio                  |
| PUT    | /api/servicios/:id                 | Actualizar servicio             |
| DELETE | /api/servicios/:id                 | Desactivar servicio             |
| GET    | /api/usuarios                      | Listar usuarios                 |
| POST   | /api/usuarios                      | Crear usuario                   |
| PUT    | /api/usuarios/:id                  | Actualizar usuario              |
| DELETE | /api/usuarios/:id                  | Desactivar usuario              |
| POST   | /api/usuarios/:id/servicios        | Asignar servicios a agente      |
| GET    | /api/usuarios/catalyst             | Listar usuarios Catalyst Auth   |
| GET    | /api/admin/dashboard?sede_id=X     | Dashboard en tiempo real        |
| GET    | /api/admin/reportes?filtros        | Reportes historicos             |
| GET    | /api/admin/reportes/csv?filtros    | Exportar reportes en CSV        |

---

## Reglas de Negocio

1. **Turnos secuenciales por sede y dia**: El numero secuencial se reinicia automaticamente cada dia a las 00:00 (se calcula por fecha)
2. **Servicios asignados**: Un agente solo puede llamar turnos de servicios que tiene asignados
3. **Un turno activo a la vez**: Un agente solo puede tener 1 turno en estado 'llamado' o 'en_atencion'
4. **Re-llamadas**: Maximo 3 re-llamadas; despues se marca automaticamente como 'no_se_presento'
5. **Modulos publicos**: Generar Turno y Pantalla Publica no requieren autenticacion
6. **Modulos protegidos**: Panel de Atencion (agente/admin) y Panel Administrativo (solo admin)
7. **Filtro por sede**: Un agente solo opera sobre la sede asignada

---

## Despliegue Paso a Paso

### 1. Requisitos previos

- Cuenta en [Zoho Catalyst](https://catalyst.zoho.com/)
- CLI de Catalyst instalado: `npm install -g zcatalyst-cli`
- Proyecto creado en la consola de Catalyst

### 2. Configurar Authentication

1. Catalyst Console → tu proyecto → **Authentication**
2. Habilitar **Email Authentication**
3. En **Allowed Redirect URLs** agregar:
   - `https://TU-PROYECTO.development.catalystserverless.com/app/agente.html`
   - `https://TU-PROYECTO.development.catalystserverless.com/app/admin.html`
   - `https://TU-PROYECTO.development.catalystserverless.com/app/index.html`
4. Guardar cambios

### 3. Crear tablas en Data Store

1. Catalyst Console → **Data Store**
2. Crear las 6 tablas exactamente como se describen arriba
3. Para cada tabla, agregar las columnas con los tipos indicados
4. **Importante**: Los nombres de tablas y columnas son case-sensitive

### 4. Instalar dependencias

```bash
cd functions/digiturno_function
npm install
```

### 5. Actualizar URL base

En `client/main.js`, actualizar la URL de tu proyecto:
```
https://TU-PROYECTO-ID.development.catalystserverless.com
```

### 6. Desplegar

```bash
zcatalyst deploy
```

### 7. Registrar primer admin

1. Acceder a `https://TU-PROYECTO.catalystserverless.com/__catalyst/auth/signup`
2. Registrarte con tu email
3. En Data Store → tabla **Usuario**, insertar manualmente:
   - `catalyst_user_id`: Tu User ID (ver en Authentication → Users)
   - `nombre`: Tu nombre
   - `email`: Tu email
   - `rol`: `admin`
   - `modulo_atencion`: `1`
   - `activo`: `true`
   - `estado`: `activo`
4. Acceder a `/app/admin.html` e iniciar sesion

### 8. Configurar el sistema

1. Crear **sedes** desde el panel admin
2. Crear **servicios** desde el panel admin
3. Registrar nuevos usuarios (se registran en Catalyst Auth)
4. Crear **perfiles de agente** desde admin, asignarles sede y servicios
5. Los agentes acceden a `/app/agente.html`
6. Los clientes acceden a `/app/cliente.html?sede=nombre-sede`
7. Las pantallas publicas se configuran en `/app/pantalla.html?sede=nombre-sede`

---

## URLs de Acceso

| Modulo                | URL                                | Auth     |
|-----------------------|------------------------------------|----------|
| Landing               | /app/index.html                    | No       |
| Generar Turno         | /app/cliente.html?sede=NOMBRE      | No       |
| Pantalla Publica      | /app/pantalla.html?sede=NOMBRE     | No       |
| Panel de Atencion     | /app/agente.html                   | Agente   |
| Panel Administrativo  | /app/admin.html                    | Admin    |

---

## Comunicacion en Tiempo Real

El sistema usa **polling** (consultas periodicas al servidor):

- **Pantalla publica**: cada 3 segundos
- **Notificaciones cliente**: cada 3 segundos (despues de generar turno)
- **Cola del agente**: cada 5 segundos

---

## Flujo de un Turno

```
[Cliente genera turno] → estado: espera
        ↓
[Agente llama siguiente] → estado: llamado (llamadas=1)
        ↓
   ┌─ [Re-llamar] → llamadas++ (max 3, luego auto no_se_presento)
   ├─ [No se presento] → estado: no_se_presento
   └─ [Atender] → estado: en_atencion
        ↓
[Agente finaliza] → estado: finalizado
```

---

## Stack Tecnologico

- **Backend**: Node.js (Zoho Catalyst Functions - serverless)
- **Base de datos**: Zoho Catalyst DataStore
- **Autenticacion**: Zoho Catalyst Authentication nativa
- **Frontend**: HTML + CSS + JavaScript vanilla (sin frameworks)
- **Comunicacion**: Polling HTTP cada 3-5 segundos
