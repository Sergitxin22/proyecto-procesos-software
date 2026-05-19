# Documentación del Proyecto

Bienvenido a la documentación de **FlexiLearn** (Proyecto de Procesos de Software).

Esta página estática se genera automáticamente usando Docusaurus.

## ¿Qué encontrarás aquí?

En el menú de navegación de arriba encontrarás:
1. **Documentación del Proyecto (Docs):** Donde se incluyen los manuales de usuario, arquitectura, y decisiones del proyecto. Puedes crear más archivos `.md` en la carpeta `docs/` de tu código para que aparezcan aquí.
2. **Javadoc (API Backend):** Un enlace directo a la documentación del código fuente generada automáticamente a partir de los comentarios de Java (a través de la herramienta de Gradle).
3. **Swagger (API REST):** Para ver la documentación de la API en vivo, deberás ejecutar el backend (`./gradlew bootRun`) e ir a `http://localhost:8080/swagger-ui.html`.

Esta documentación se compila y publica automáticamente en GitHub Pages con cada push a la rama `main`, integrando el Javadoc y esta web dentro del mismo sitio web.
