# FlexiLearn - Documentación del Proyecto

Bienvenido a la documentación oficial de **FlexiLearn**, desarrollada para la asignatura de Procesos de Software.

Esta página web estática se genera y mantiene automáticamente usando **Docusaurus**.

## ¿Qué encontrarás aquí?

En el menú de navegación superior tienes acceso a:

1. **Manuales y Proyecto:** Documentación general, guías de usuario, arquitectura y detalles del proyecto. (Si añades más archivos `.md` en la carpeta \`docs\` de tu repositorio, aparecerán aquí automáticamente).
2. **Referencia API:** Documentación del código fuente del backend. En lugar de un Javadoc HTML aislado, leemos los comentarios de tu código Java y los convertimos nativamente a páginas Markdown para que se democraticen e integren en Docusaurus.
3. **Swagger (API REST interactiva):** Para consultar y probar los endpoints en vivo, debes ejecutar el backend localmente (\`./gradlew bootRun\`) e ingresar a \`http://localhost:8080/swagger-ui/index.html\`.

## Gestión y Despliegue

La plataforma se despliega y actualiza sola, pero si quieres hacer pruebas en tu ordenador:

- **Desarrollo (Previsualización):** Ejecuta \`./gradlew docs\` (o \`.\gradlew.bat docs\` en Windows). Este script autogenerará el Markdown desde tus clases de Java y te arrancará el servidor web para ver los cambios en vivo en tu navegador en el puerto \`3000\`.
- **Producción:** Todo se compila de manera automática mediante *GitHub Actions* en cada push a la rama \`main\`, generando los archivos web estáticos listos para ser consumidos y actualizando GitHub Pages.
