package com.sergitxin.flexilearn.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.sergitxin.flexilearn.dto.CursoRequestDTO;
import com.sergitxin.flexilearn.dto.CursoStatsDTO;
import com.sergitxin.flexilearn.dto.EjercicioRequestDTO;
import com.sergitxin.flexilearn.dto.ForumMessageResponseDTO;
import com.sergitxin.flexilearn.dto.MessageDTO;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.dto.ModuloRequestDTO;
import com.sergitxin.flexilearn.dto.TestRequestDTO;
import com.sergitxin.flexilearn.dto.CursoUpdateDTO;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Mensaje;
import com.sergitxin.flexilearn.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
@Tag(name = "Cursos", description = "Operaciones relacionadas con la gestión de cursos de la plataforma")
/**
 * Controlador REST para la gestión de cursos.
 * Proporciona endpoints para crear, obtener, actualizar y eliminar cursos,
 * así como interactuar con módulos, ejercicios y foros.
 */
public class CursoController {

    private final CursoService cursoService;

    /**
     * Construye un nuevo controlador REST para cursos gestionando operaciones a través del servicio correspondiente.
     * @param cursoService El servicio que gestiona la lógica de negocio subyacente para los cursos.
     */
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @Operation(summary = "Crea un curso", description = "Añade en la base de datos un curso con los datos introducidos")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/")
    /**
     * Crea y registra un nuevo curso en el sistema asociado al usuario que lo crea.
     * @param authHeader El token de autorización del usuario que actúa como profesor/creador.
     * @param request El conjunto de datos necesarios (DTO) para la creación del curso.
     * @return 200 (OK) con el identificador o respuesta de éxito al crear el curso.
     */
    public ResponseEntity<?> createCurso(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody CursoRequestDTO request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
            
        String token = authHeader.substring(7);

        return ResponseEntity.ok(cursoService.crearCurso(token, request.getNombre(), request.getCategoria(), request.getDescripcion(), Dificultad.stringToDificultad(request.getDificultad())));
    }

    @Operation(summary = "Añade un módulo a un curso", description = "Añade en la base de datos un módulo a un curso con los datos introducidos")
    @PostMapping("/modules")
    /**
     * Agrega un nuevo módulo de contenido a un curso ya existente.
     * @param request El objeto que encapsula la información del módulo y el ID del curso relacionado.
     * @return 200 (OK) con el identificador único (Long) del módulo recién generado.
     */
    public ResponseEntity<Long> createModulo(@RequestBody ModuloRequestDTO request) {
        return ResponseEntity.ok(cursoService.crearModulo(request.getNombre(), request.getDescripcion(), request.getIdCurso()));
    }

    @Operation(summary = "Añade un ejercicio a un módulo", description = "Añade en la base de datos un ejercicio a un módulo con los datos introducidos")
    @PostMapping("/exercises")
    /**
     * Crea un nuevo ejercicio (teoría, código, etc.) y lo asocia a su respectivo módulo.
     * @param request DTO con los detalles del ejercicio, puntaje y lenguaje.
     * @return 200 (OK) con el número (Long) de ID del componente recién insertado.
     */
    public ResponseEntity<Long> createEjercicio(@RequestBody EjercicioRequestDTO request) {
        return ResponseEntity.ok(cursoService.crearEjercicio(request.getNombre(), request.getTeoria(), request.getCodigoInicial(), request.getPuntos(), request.getEnunciado(), request.getLenguaje(), request.getIdModulo()));
    }

    @Operation(summary = "Añade tests a un ejercicio", description = "Guarda tests para un ejercicio existente")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/exercices/{idExercise}/tests")
    /**
     * Vincula un conjunto de validaciones (tests) a un ejercicio puntual en el servidor.
     * @param authHeader Token validando los permisos para operar.
     * @param idExercise Identificador del ejercicio correspondiente que será probado.
     * @param tests Lista JSON de parámetros de pruebas para el ejercicio.
     * @return 200 (OK) con la confirmación de la inserción, o 401 si falla la autorización.
     */
    public ResponseEntity<?> createExerciseTests(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("idExercise") Long idExercise,
            @RequestBody List<TestRequestDTO> tests) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(cursoService.crearTestsEjercicio(token, idExercise, tests));
    }

    @GetMapping("/{id}/")
    /**
     * Obtiene los detalles de un curso específico dado su ID.
     * @param id Identificador del curso.
     * @return El curso correspondiente al ID.
     */
    public ResponseEntity<Curso> getCourse(
	@PathVariable("id") Long id) {
    	return ResponseEntity.ok(cursoService.getCurso(id));
    }

    @GetMapping("exercises/{id}")
    /**
     * Obtiene los detalles de un ejercicio específico dado su ID.
     * @param id Identificador del ejercicio.
     * @return El ejercicio correspondiente al ID.
     */
    public ResponseEntity<Ejercicio> getExercise(
	@PathVariable("id") Long id) {
    	return ResponseEntity.ok(cursoService.getExercise(id));
    }

    @Operation(summary = "Obtiene los ejercicios del módulo al que pertenece un ejercicio",
               description = "Dado el ID de un ejercicio, devuelve la lista ordenada de ejercicios de su módulo")
    @GetMapping("exercises/{id}/module-exercises")
    /**
     * Entrega todos los ejercicios compañeros y pertenecientes a un mismo módulo en base a uno de sus IDs de ejercicio.
     * @param id El ID del ejercicio cuyo módulo se desea consultar.
     * @return Lista JSON con todos los Ejercicios de dicho módulo en código 200 (OK).
     */
    public ResponseEntity<List<Ejercicio>> getModuleExercises(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cursoService.getEjerciciosDelModulo(id));
    }

    @Operation(summary = "Obtener cursos", description = "Obtiene todos los cursos")
    @GetMapping("/")
    /**
     * Consulta el catálogo global de cursos disponibles.
     * @return Una respuesta con la lista de objetos de Curso (200 OK).
     */
    public ResponseEntity<List<Curso>> getCourses() {
    	return ResponseEntity.ok(cursoService.getAllCursos());
    }
    
    @Operation(summary = "Matricula al usuario en un curso")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/enroll")
    /**
     * Inscribe oficialmente al usuario solicitante en el curso especificado.
     * @param authHeader Token de acceso Bearer del estudiante o usuario.
     * @param cursoId Referencia identificadora del curso a matricularse.
     * @return Una respuesta de éxito 200 detallando el logro o 401 si no está autenticado.
     */
    public ResponseEntity<?> enrollCourse(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long cursoId) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        String token = authHeader.substring(7);
        cursoService.matricularUsuario(token, cursoId);
        return ResponseEntity.ok(new MessageResponseDto("Matriculado correctamente"));
    }

    @Operation(summary = "Obtiene los cursos en los que está matriculado el usuario")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/enrolled")
    /**
     * Genera un listado de los cursos activos en los que se encuentra participando el usuario emitivo.
     * @param authHeader El token Bearer validando la sesión.
     * @return 200 (OK) con un arreglo de cursos del usuario. 401 si falla el token.
     */
    public ResponseEntity<?> getEnrolledCourses(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        String token = authHeader.substring(7);
        return ResponseEntity.ok(cursoService.getCursosMatriculados(token));
    }

    @Operation(summary = "Eliminar curso", description = "El profesor elimina un curso suyo para que los alumnos dejen de tener acceso")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/deleteCurso")
    /**
     * Borra permanentemente un curso asumiendo que el usuario del token es el dueño creador.
     * @param authHeader Token autenticador con los privilegios comprobables de pertenencia.
     * @param cursoId Identidad de la materia a erradicar (Long).
     * @return 1 dentro del cuerpo 200 OK en caso de éxito, 0 en caso de fallar si no es el creador.
     */
    public ResponseEntity<?> deleteCurso(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestParam Long cursoId){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
            
        String token = authHeader.substring(7);
        
        boolean eliminado = cursoService.eliminarCursoDelProfesor(token, cursoId);
       if (eliminado) {
        return ResponseEntity.ok(1);
        }
        return ResponseEntity.ok(0); 
    }

    @Operation(summary = "Obtiene los puntos totales de un curso", description = "Suma los puntos de todos los ejercicios de todos los módulos del curso")
    @GetMapping("/{id}/puntos")
    /**
     * Calcula la experiencia o puntos totales posibles que brinda un curso.
     * @param cursoId El ID del cual queremos saber el límite de puntos en total.
     * @return El entero numérico (200 OK) que expone los puntos sumados de los ejercicios.
     */
    public ResponseEntity<Integer> getTotalPuntos(@PathVariable("id") Long cursoId) {
        return ResponseEntity.ok(cursoService.getTotalPuntosByCurso(cursoId));
    }

    @Operation(summary = "Obtiene los puntos completados por el usuario en un curso", description = "Suma los puntos de los ejercicios que el usuario autenticado ha completado en el curso dado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/mis-puntos")
    /**
     * Extrae cuántos puntos el propio estudiante se ha ganado en una disciplina concreta.
     * @param authHeader El pase necesario del usuario estudiante (token HTTP Bearer).
     * @param cursoId Identidad para ubicar al curso de la base de datos de consulta.
     * @return Devuelve código 200 con la puntuación recolectada con éxito.
     */
    public ResponseEntity<?> getMisPuntos(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long cursoId) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        String token = authHeader.substring(7);
        return ResponseEntity.ok(cursoService.getPuntosCompletadosEnCurso(token, cursoId));
    }

    @Operation(summary = "Enviar mensaje al foro de un curso")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/messages")
    /**
     * Inserta un nuevo mensaje dentro del foro comunitario de un curso en particular.
     * @param authHeader Llave JWT del remitente identificando quién envía el mensaje.
     * @param cursoId Identidad local del curso donde se agrupa el mensaje.
     * @param mensaje Cuerpo textual del comentario enviado por el usuario.
     * @return 200 (OK) en un ForumMessageResponseDTO detallado con los datos de creación, o error si fracasa la validación.
     */
    public ResponseEntity<ForumMessageResponseDTO> sendMessage(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long cursoId,
            @RequestBody MessageDTO mensaje) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        String token = authHeader.substring(7);
        Mensaje message = cursoService.guardarMensaje(token, mensaje.getMensaje(), cursoId);
        if (message == null) {
            return ResponseEntity.badRequest().build();
        }

        ForumMessageResponseDTO mensajeDTO = new ForumMessageResponseDTO();
        mensajeDTO.setDate(message.getFecha().toString());
        mensajeDTO.setMensaje(message.getTexto());
        mensajeDTO.setUsername(message.getUsuario().getNombre());
        return ResponseEntity.ok(mensajeDTO);
    }

    @Operation(summary = "Obtener mensajes de un curso")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/messages")
    /**
     * Reúne todos los mensajes mandados por cualquier integrante del foro en un curso concreto.
     * @param authHeader El JWT Bearer para autorizar que la lectura de foro se da de forma válida.
     * @param cursoId Referencia a dicho curso del que tomamos mensajes.
     * @return Lista (JSON 200) de mensajes formados detalladamente (usuario, fecha, texto) ordenados.
     */
    public ResponseEntity<List<ForumMessageResponseDTO>> getMessages(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
        @PathVariable("id") Long cursoId) {
        String token = authHeader.substring(7);
        List<Mensaje> mensajes = cursoService.getMessages(cursoId, token);
        List<ForumMessageResponseDTO> messages = new ArrayList<>();
        for (Mensaje mensaje : mensajes) {
            ForumMessageResponseDTO mensajeDTO = new ForumMessageResponseDTO();
            mensajeDTO.setMensaje(mensaje.getTexto());
            mensajeDTO.setUsername(mensaje.getUsuario().getNombre());
            mensajeDTO.setDate(mensaje.getFecha().toString());
            messages.add(mensajeDTO);
        } 

    	return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Obtiene estadísticas del curso", description = "Devuelve estadísticas como cantidad de alumnos, ejercicios y el progreso de cada alumno")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/stats")
    /**
     * Entrega información analítica agregada con datos del curso como participantes o ejercicios.
     * @param id Identidad del recurso de curso evaluado.
     * @param authorizationHeader Componente Header incluyendo el JWT verificado por seguridad.
     * @return Una respuesta DTO con los detalles del alcance en 200 OK.
     */
    public ResponseEntity<CursoStatsDTO> getCourseStats(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        String token = authorizationHeader.replace("Bearer ", "");
        CursoStatsDTO stats = cursoService.getCourseStats(token, id);
        
        if (stats == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Actualiza un curso completo", description = "Actualiza los datos del curso, sus módulos y ejercicios")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    /**
     * Aplica nuevas modificaciones generales a la configuración principal de un curso previamente alojado.
     * @param authHeader El código de sesión del profesor (token).
     * @param cursoId Modificando recurso en este ID.
     * @param cursoUpdateDTO Payload principal con la versión nueva de nombre, etc.
     * @return El recurso reconstruido si éxito (200), u otros mensajes de error internos o autoritativos.
     */
    public ResponseEntity<?> updateCurso(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long cursoId,
            @RequestBody CursoUpdateDTO cursoUpdateDTO) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
        
        String token = authHeader.substring(7);
        
        try {
            Curso cursoActualizado = cursoService.actualizarCurso(token, cursoId, cursoUpdateDTO);
            return ResponseEntity.ok(cursoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponseDto(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponseDto("Error al actualizar el curso: " + e.getMessage()));
        }
    }
}