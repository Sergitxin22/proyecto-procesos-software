# Flexilearn - Sistema de Gestión de Aprendizaje (LMS)

Flexilearn es un proyecto integral de plataforma educativa (LMS) que abarca no solo un sólido sistema de autenticación, sino también la gestión completa de cursos, módulos, ejercicios y perfiles de usuario. Este documento explica el flujo general de la aplicación, su arquitectura orientada a servicios y las integraciones tecnológicas aplicadas.

## Arquitectura y Capas

El backend está construido con **Spring Boot (Java)** siguiendo una arquitectura en capas para separar responsabilidades adecuadamente y un frontend reactivo mediante **React + Vite**:

1. **Controller / Facade:** Define los puntos de entrada (p.ej., `AuthController`, `CursoController`) y se comunica mediante REST API y DTOs generados e interceptados.
2. **Service / Lógica de Negocio:** Encarga de todo el flujo (creación de usuarios, creación de cursos, asociación de módulos y validaciones).
3. **Repository / DAO (`UsuarioDao`, `CursoDAO`, `ModuloDAO`, `EjercicioDAO`):** Interfaces que se comunican con persistencia (vía Spring Data JPA).
4. **Entity:** Las clases de persistencia representativas de la lógica del LMS (`Usuario`, `Curso`, `Modulo`, `Ejercicio`).
5. **Port & Adapter / Factory (`AuthExternalFactory`):** Define un punto de conexión hacia sistemas externos (Google, GitHub) para en un futuro delegar la validación de tokens o Single Sign-On.

---

## Funcionalidades Principales

El proyecto cubre los siguientes módulos:

### 1. Gestión de Autenticación
- **Registro:** Crea un usuario validando credenciales y correo en BD.
- **Login:** Autentica cuentas y genera tokens (actualmente se integran pruebas del patrón Factory simulando validaciones tipo Google/GitHub SSO).
- **Logout:** Permite invalidar sesiones registradas en BD.

### 2. Gestión de Cursos (LMS)
- Creación, edición, y listado de Cursos.
- Creación y asociación de **Módulos** por cada curso asignado.
- Creación de **Ejercicios** dentro de cada módulo.
- Existen roles como Profesor y Administrador para el control de la información (ver `frontend/src/pages/Courses/` y `Admin/`).

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

## Panel Frontend (React + Vite)

El proyecto incluye una Interfaz de Usuario robusta en la carpeta `/frontend` construida con **React JS**. 

### Características del Frontend:
- **UI Moderna**: Desarrollada mediante CSS puro estructurado en Tarjetas (Cards), utilizando un sistema de diseño minimalista con notificaciones.
- **Rutas y Vistas**:
  - `Landing Page / Dashboard`: Vista principal inicial de bienvenida/panel.
  - `Autenticación`: Formularios Reactivos para Registro e Inicio de sesión.
  - `Cursos`: Vistas para visualizar listados (`CreatedCourses`), o herramientas para docentes (`CreateCourse`, `CreateModule`, `CreateExercise`).
  - `Panel Admin`: Espacios de control, por ejemplo un visualizador de usuarios (`UserList`).
  - `Perfil`: Vista de usuario individual (`Profile`).
- **Gestor de Sesión**: Contiene un módulo "Estado Actual" que verifica si tienes un JWT/Token vivo, e incluye la invocación con el Token hacia el final de la sesión.
- **Integración API/CORS**: Comunicación fluida con los endpoints de Spring Boot mediante Axios o Fetch integrados en `/services/api.service.js`.

### Cómo ejecutar los entornos:

Dado que hay varias tecnologías conviviendo en paralelo, se deben arrancar en **ventanas de terminal separadas**:

**1. Levantar la Base de Datos (Docker)**
Abre una terminal de linux (wsl) en la carpeta principal del proyecto y levanta el contenedor de la base de datos:
```bash
wsl
cd docker
docker compose up -d
```

**2. Levantar el Backend (Spring Boot)**
Abre una terminal en la carpeta principal del proyecto y ejecuta:
```bash
gradlew bootRun
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
