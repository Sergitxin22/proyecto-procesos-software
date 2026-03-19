# Flexilearn - Sistema de Autenticación

Este documento explica el flujo de autenticación implementado en el proyecto Flexilearn, abarcando desde la capa de exposición web hasta la base de datos y la integración de validaciones externas mediante el patrón Factory.

## Arquitectura y Capas

El flujo de autenticación está construido siguiendo una arquitectura en capas para separar responsabilidades adecuadamente:

1. **Controller / Facade (`AuthController`):** Es el punto de entrada de la aplicación. Expone las rutas REST documentadas con Swagger/OpenAPI y procesa los DTOs de entrada y salida (`Request` y `Response`).
2. **Service (`AuthService`):** Contiene la lógica de negocio central de la aplicación (creación de usuarios, validaciones y generación/eliminación de tokens).
3. **Repository / DAO (`UsuarioDao`):** Interfaz que se comunica con la base de datos en memoria (H2) utilizando Spring Data JPA para consultar y persistir entidades.
4. **Entity (`Usuario`):** Representa la tabla `usuarios` en la base de datos donde se almacenan persistentes las credenciales y el token de la sesión activa.
5. **Port & Adapter / Factory (`AuthExternalFactory`):** Define un punto de conexión hacia sistemas externos (Google, GitHub) para en un futuro delegar la validación de tokens de terceros.

---

## Flujo de Operaciones 

El API expone tres endpoints principales bajo el decorador de Swagger `@Tag(name = "Autenticación")`.

### 1. Registro de Usuario (`POST /api/auth/registro`)
**Objetivo:** Crear un nuevo usuario en el sistema.
- El cliente envía la petición con el `RegisterRequestDto` (`nombre`, `email`, `password`).
- El `AuthController` delega la petición al `AuthService`.
- Se revisa mediante `UsuarioDao.existsByEmail(...)` si el usuario no existe. Si es libre, se guarda la nueva entidad en la base de datos.
- Devuelve un código HTTP `201 Created` si tuvo éxito o un `400 Bad Request` si el correo ya estaba registrado.

### 2. Inicio de Sesión (`POST /api/auth/login`)
**Objetivo:** Validar las credenciales de un usuario y generarle un token de acceso a la plataforma.
- El cliente envía credenciales mediante el `LoginRequestDto` (`email`, `password`).
- El servicio consulta la base de datos (`findByEmail`) comparando contraseñas.
- Si las credenciales son válidas, se genera un ID único mediante `UUID` que fungirá como `token`.
- El token es almacenado **físicamente en la base de datos** asociado a ese Usuario para mantener la sesión.
- Se responde con un HTTP `200 OK` devolviendo un `LoginResponseDto` junto con el Token.
- *Bonus del Patrón Factory:* Justo al iniciar la ejecución de este método se dispara una prueba interna hacia el `AuthExternalFactory` (ver la sección de Factory más abajo).

### 3. Cierre de Sesión (`POST /api/auth/logout`)
**Objetivo:** Invalidar el token activo rompiendo la sesión del usuario.
- El cliente o sistema envía una petición con el `LogoutRequestDto` incluyendo su `token` actual.
- El `AuthService` hace una búsqueda en la base de datos usando `UsuarioDao.findByToken(...)`.
- Al localizar a quién le pertenece ese token, el servicio reescribe el campo con `null` y guarda la entidad, lo que borra efectivamente la sesión activa remanente.

---

## Implementación del Patrón Factory (Validadores Externos)

Como parte de la estructura hemos implementado un patrón de diseño **Abtract Factory / Factory Method** preparado para habilitar single-sign-on (SSO) con proveedores externos.

1. Hemos creado una interfaz (Puerto de salida) de nombre `AuthExternalPort` que expone un único contrato llamado `validarTokenExterno`.
2. Se definieron múltiples adaptadores a esta interfaz: `GoogleAuthExternalAdapter` y `GithubAuthExternalAdapter`.
3. Hemos definido los tipos de proveedores soportados en el enum `AuthProvider`.
4. El archivo principal de la estrategia es el inyectable `AuthExternalFactory`. Su propósito es construir y devolver el adaptador correcto dinámicamente dependiendo del Enum de entrada utilizando cláusulas `switch`.

### Probando el Factory en la Práctica
Actualmente hay un test "hardcodeado" incrustado el momento de validar el Login (`POST /api/auth/login`).

Para verlo en acción:
1. Inicia en local tu proyecto en Spring Boot.
2. Usando SwaggerUI, lanza una petición hacia `/login`. 
3. Revisa la consola interna de Spring Boot. Se verá el siguiente registro demostrando el funcionamiento inyectado con Google:
   ```
   Validando token con Google OAuth2 API...
   ¿Es válido el token en el proveedor externo? true
   ```
4. Si cambias el `AuthService` para que llame a `AuthProvider.GITHUB`, la salida automática cambiará hacia la estrategia de la API de Github.

---

## Panel Frontend de Pruebas (React + Vite)

Para facilitar la interacción con los endpoints del API sin depender únicamente de Swagger, el proyecto cuenta con un cliente web integrado en la carpeta `/frontend` construido con React JS.

### Características del Frontend:
- **UI Moderna**: Desarrollada mediante CSS puro estructurado en Tarjetas (Cards), utilizando un sistema de diseño minimalista con notificaciones (toasts) que se auto-ocultan.
- **Formularios Reactivos**: Manejo de estados de Registro de nuevos usuarios y confirmación visual de credenciales inválidas.
- **Gestor de Sesión**: Contiene un módulo "Estado Actual" que verifica si tienes un JWT/Token vivo, e incluye la invocación con el Token hacia el final de la sesión (`/api/auth/logout`).
- **Integración CORS Completa**: El `AuthController` de backend en Spring Boot contiene la directiva `@CrossOrigin(origins = "*")` permitiendo el puente entre `http://localhost:5173` (Vite) hacia el puerto `8080` de Spring.

### Cómo ejecutar los entornos:

Dado que hay varias tecnologías conviviendo en paralelo, se deben arrancar en **ventanas de terminal separadas**:

**1. Levantar la Base de Datos (Docker)**
Abre una terminal de linux (wsl) en la carpeta principal del proyecto y levanta el contenedor de la base de datos:
```bash
wsl
cd docker
docker compose up -d
exit
```

**2. Levantar el Backend (Spring Boot)**
Abre una terminal en la carpeta principal del proyecto y ejecuta:
```bash
./gradlew bootRun
```
*Esto activará el API en el puerto `8080`.*

**3. Levantar el Frontend (React)**
Una vez que el backend esté arriba, abre otra ventana de terminal en VS Code y navega al cliente web y arráncalo:
```bash
cd frontend
npm install  # (Solo la primera vez si no lo has clonado o instalado antes)
npm run dev
```
*Se desplegará una ruta local (como `http://localhost:5173`). Haz click sobre ella para probar el sistema visual.*
