# Flexilearn - Sistema de Gestión de Aprendizaje (LMS)

Flexilearn es un proyecto integral de plataforma educativa interactiva (LMS) que abarca gestión completa de autenticación, administración de cursos, módulos, ejercicios, perfiles de usuario y roles administrativos. Este documento describe en detalle la arquitectura, tecnologías clave establecidas y las instrucciones exactas de ejecución en todos sus modos.

## 🛠️ Tecnologías y Arquitectura

El sistema está construido siguiendo un esquema cliente-servidor, con tecnologías modernas para facilitar su mantenimiento y escalabilidad.

### ⚙️ Backend (Spring Boot 3 + Java)
La API REST funciona bajo una estructura multicapas (Arquitectura Hexagonal/Capas):
1. **Controller / Facade:** Define los puntos de entrada (p.ej., `AuthController`, `CursoController`) recibiendo y despachando DTOs (Data Transfer Objects).
2. **Service / Lógica de Negocio:** Encargada de agrupar todo el flujo de trabajo funcional y algoritmos del sistema (validaciones, hasheo de contraseñas, etc.).
3. **Repository / DAO:** Capa de persistencia haciendo uso de **Spring Data JPA**. Gestiona interfaces como `UsuarioDao`, `CursoDAO`, `ModuloDAO` y `EjercicioDAO`.
4. **Entity:** Modelado relacional mapeado a base de datos (`Usuario`, `Curso`, `Modulo`, `Ejercicio`).
5. **Port & Adapter / Factory:** Se diseñó un patrón *Abstract Factory* (`AuthExternalFactory`) preparado para delegar autenticaciones hacia sistemas SSO externos (Google/GitHub).

### 🖥️ Frontend (React 18 + Vite)
El panel cliente está desarrollado como una SPA (Single Page Application) en la carpeta `/frontend`:
- **Tecnologías:** React, Vite para bundler ultrarrápido, React Router para navegación, CSS responsivo basado en Cards y Axios/Fetch para consumir la API.
- **Rutas Principales:**
  - `Landing Page`: Presentación del sistema o plataforma.
  - `Autenticación`: Formularios para login y registro (`Auth.jsx`, `TestAuth.jsx`).
  - `Cursos`: Creación, despliegue y edición de estructura (Módulos/Ejercicios) para el rol de profesor y estudiante.
  - `Admin`: Panel exclusivo de gestión global (`UsersList`).
  - `Profile`: Panel personal por cuenta.

### 💾 Base de Datos (PostgreSQL + Docker)
El sistema emplea una instancia íntegra en **PostgreSQL**, provisionada automáticamente mediante contenedores Docker, e incluye un `DataInitializer` preconfigurado en Spring para inyectar cuentas locales base.

#### 📊 Modelo de Datos (Diagrama E-R)
La estructura relacional principal que soporta toda la lógica de los cursos, módulos, ejercicios y usuarios, se resume en el siguiente diagrama:

![Diagrama Entidad-Relación](src/main/resources/static/Diagrama%20E-R.png)

---

## 🌟 Funcionalidades Principales

1. **Gestión de Autenticación y Seguridad:**
   - Registro de usuarios con hasheo de contraseñas de alta seguridad.
   - Login por validaciones locales con generación de **Tokens de sesión en base de datos**.
   - Integración funcional del Patrón Factory para testing de validadores de tokens externos.
2. **Gestión Estructural de Cursos (LMS):**
   - Sistema de jerarquía pura: Cursos → Módulos → Ejercicios.
   - Perfil de estudiante vs. Creador de Curso.
   - Entorno precargado con cursos de ejemplo (Introducción a Java, Spring Boot Avanzado), organizados con módulos didácticos y ejercicios interactivos puntuados que se realizan en la misma aplicación web, soportadas por el motor de ejecución de [Piston](https://github.com/engineer-man/piston).
3. **Gestión de Usuarios y Roles:**
   - Usuarios base generados automáticamente (`aitor@aitor.com`, `markel@markel.com` como usuarios estándar; `aroa@aroa.com` como **admin**).
   - Panel de control de administradores.

## 📁 Estructura del Proyecto

El repositorio está organizado en varios módulos clave para mantener la separación entre cliente y servidor.

```text
proyecto-procesos-software/
├── docker/                 # Archivos de orquestación (Contenedores y BD Postgres)
|   ├── frontend/           # Contiene el Dockerfile de la imagen del frontend
|   ├── backend/            # Contiene el Dockerfile de la imagen del backend
│   ├── deploy.yaml         # Compose pre-configurado para producción
│   └── compose.yaml        # Compose clásico local de DB, para desarrollo
├── frontend/               # Aplicación cliente React + Vite
│   ├── src/
│   │   ├── components/     # Piezas visuales reusables (Navbar, Cards)
│   │   ├── pages/          # Vistas (Admin, Autenticación, Cursos)
│   │   ├── services/       # Módulo API para consumir el backend
│   │   └── assets/         # Recursos públicos de la página
├── src/                    # Código fuente del Backend Spring Boot (Java)
│   ├── main/java/.../flexilearn/
│   │   ├── dao/            # Data Access Objects (JPA Repositories)
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # Modelos de Base de Datos
│   │   ├── service/        # Lógica de Negocio Central
│   │   └── facade/         # Controladores REST API
│   └── main/resources/     # Opciones globales de JPA, PostgreSQL e imágenes
└── build.gradle            # Gestor de dependencias backend
```

---

## 🚀 Guía de Despliegue y Ejecución

### Prerrequisitos
Para ejecutar y colaborar en este proyecto, necesitas instalar el siguiente software en tu entorno local:
- **Java JDK 25** (para compilar y ejecutar el backend de Spring Boot).
- **Node.js (18+) y NPM** (para la gestión de dependencias y ejecución del frontend).
- **Docker y Docker Compose** (para levantar la base de datos y/o los contenedores orquestados).
- Entorno **Linux/macOS** o **WSL 2** (Windows Subsystem for Linux), altamente recomendado.

---

El proyecto soporta dos modos de ejecución, dependiendo de la etapa en la que te encuentres:

### Opción A: Entorno de Desarrollo (Develop / Hot-Reload)
Ideal para modificar código en tiempo real (con _hot-reloading_ en React y modo debug para el API). Se deben arrancar sus 3 fragmentos vitales en **ventanas de terminal separadas**:

**Paso 1: Levantar la Base de Datos (PostgreSQL) y PistonAPI**
Abre una terminal de linux (como WSL) en la raíz del proyecto y enciende el contenedor:
```bash
wsl
cd docker
docker compose up -d
```

**Paso 2: Levantar el Backend (Spring Boot)**
Abre una segunda terminal en la raíz del proyecto y ejecuta:
```bash
./gradlew bootRun
```
*(En terminales de comandos Windows tradicionales usa `gradlew.bat bootRun`). Su arranque exitoso conectará Spring con la BD en el puerto `8080` e inyectará los usuarios de prueba automáticamente si está vacía.*

**Paso 3: Levantar el Frontend (React)**
Una vez que el backend esté arriba, abre una tercera ventana de terminal, dirígete al cliente web y arráncalo:
```bash
cd frontend
npm install  # (Solo la primera vez para instalar dependencias desde package.json)
npm run dev
```
*Se desplegará una ruta local (usualmente `http://localhost:5173`). Haz clic sobre ella para disfrutar visualmente de Flexilearn en tu navegador.*

#### 📄 Documentación API (Swagger UI)
Ya sea que inicies en modo desarrollador o de producción, el backend en Sprint Boot autogenera la tabla visual de los endpoints disponibles al momento de ejecutarse.
Puedes lanzar consultas crudas hacia la API accediendo al entorno integrado de Swagger en:
> **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

### Opción B: Entorno de Producción (Deploy Orquestado / Full Docker)
Ideal para probar su instalación en un servidor de producción. Este método **obvia las instalaciones de Node y Java**, envolviendo por completo backend y frontend y base de datos con `deploy.yaml`.

⚠️ **Importante antes de desplegar:** Tienes que modificar la configuración para poder conectar con los contenedores.
1. En el archivo del backend `application.properties` (propiedad `spring.datasource.url`), donde pone `"localhost"` debes cambiarlo a `"db"` (el nombre del servicio de la base de datos en Docker):
`spring.datasource.url=jdbc:postgresql://db:5432/postgres`

2. En el archivo `PistonGateway.java` (ruta `src/main/java/com/sergitxin/flexilearn/external/PistonGateway.java`), debes comentar la variable `API_URL` que usa `"localhost"` y descomentar la que usa `"piston"`:
`private final String API_URL = "http://piston:2000/api/v2/";`

Una vez cambiado, abre una terminal en la raíz del proyecto y ejecuta:
```bash
wsl
cd docker
docker compose -f deploy.yaml up -d
```

Una vez que el building process finalice, todo estará corriendo y sincronizado de forma autónoma:
- **Frontend web público:** [http://localhost:5173](http://localhost:5173) (redirecciona internamente sus llamadas al backend).
- **Backend API local:** `http://localhost:8080`.
- **Base de Datos Postgres:** Expuesta y persistida internamente por los volúmenes del motor Docker.
- - **Motor de ejecución de Piston:** Con los runtimes de ejecución ya instalados.

## Piston
Este proyecto implementa Piston, un entorno de ejecución aislado y seguro, para poder ejecutar el código introducido por los usuarios en la página. El docker-compose se encarga de la build.

---

## ✅ Ejecutar Todos los Tests

Para lanzar **todos los tests automatizados del proyecto** (backend, JUnit/JUnitPerf), ejecuta desde la raíz:

### Linux / macOS / WSL
```bash
./gradlew test
```

### Windows (PowerShell)
```powershell
.\gradlew.bat test
```

### Windows (CMD)
```bat
gradlew.bat test
```

### 📍 Dónde ver los resultados

Después de ejecutar los tests, puedes revisar:

- **Resultado HTML de tests (JUnit/Gradle):** `build/reports/tests/test/index.html`
- **Resultado XML de tests (para CI):** `build/test-results/test/`
- **Cobertura JaCoCo (HTML):** `build/reports/jacoco/html/index.html`
- **Cobertura JaCoCo (XML):** `build/reports/jacoco/test/jacocoTestReport.xml`

En Windows puedes abrirlos rápido así (PowerShell):

```powershell
start .\build\reports\tests\test\index.html
start .\build\reports\jacoco\html\index.html
```

---

### 🧹 Comandos Útiles (Troubleshooting y Reseteo)

Si en algún momento necesitas resetear el entorno (limpiar la base de datos, purgar usuarios o forzar una reconstrucción total del backend/frontend tras cambios drásticos), puedes utilizar los siguientes comandos de Docker estando en la ruta `/docker`:

**1. Detener todos los contenedores de desarrollo (solo Base de datos):**
```bash
docker compose down
```

**2. Resetear/Eliminar la Base de Datos al completo:**
Al hacer esto, eliminarás el volumen persistente de Postgres. La próxima vez que inicies, Spring Boot ejecutará el `DataInitializer` desde cero:
```bash
docker compose down -v
```

**3. Forzar reconstrucción de contenedores de Producción (deploy):**
Si hiciste cambios en el código y el comando `up -d` normal no los refleja, necesitas forzar a los contenedores a reconstruir las imágenes ignorando el caché:
```bash
docker compose -f deploy.yaml down
docker compose -f deploy.yaml build --no-cache
docker compose -f deploy.yaml up -d
```

**4. Resetearlo todo (Peligro - Nuke):**
Para limpiar todos los contenedores y los volúmenes de datos mapeados del entorno de producción:
```bash
docker compose -f deploy.yaml down -v
```

---

## 👥 Contribución y Buenas Prácticas

Si deseas colaborar en el repositorio o realizar un branch, sigue estas directrices:
- **Commits descriptivos:** Utiliza verbos de acción y referencias claras (ej. `feat: agregar controlador de cursos`, `fix: corregir CORS en AuthController`). Para conocer más buenas prácticas de cómo crear buenos commits puedes utilizar la web: **[Commiteando](https://commiteando.sergiomorales.dev/)**.
- **Ramas (Branches):** Trabaja en ramas independientes como `feature/nueva-vista` o `fix/bug-login` y haz un *Pull Request* hacia la rama principal.

---

## 📄 Licencia

Este proyecto está desarrollado bajo la licencia **MIT**. Puedes usar, copiar, modificar, fusionar, publicar, distribuir, sublicenciar y/o vender copias del software bajo las condiciones estipuladas.
