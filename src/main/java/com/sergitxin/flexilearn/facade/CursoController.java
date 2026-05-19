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
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @Operation(summary = "Crea un curso", description = "Añade en la base de datos un curso con los datos introducidos")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/")
    public ResponseEntity<?> createCurso(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @Parameter(description = "Datos para crear el nuevo curso") @RequestBody CursoRequestDTO request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
            
        String token = authHeader.substring(7);

        return ResponseEntity.ok(cursoService.crearCurso(token, request.getNombre(), request.getCategoria(), request.getDescripcion(), Dificultad.stringToDificultad(request.getDificultad())));
    }

    @Operation(summary = "Añade un módulo a un curso", description = "Añade en la base de datos un módulo a un curso con los datos introducidos")
    @PostMapping("/modules")
    public ResponseEntity<Long> createModulo(@Parameter(description = "Datos para crear el nuevo módulo") @RequestBody ModuloRequestDTO request) {
        return ResponseEntity.ok(cursoService.crearModulo(request.getNombre(), request.getDescripcion(), request.getIdCurso()));
    }

    @Operation(summary = "Añade un ejercicio a un módulo", description = "Añade en la base de datos un ejercicio a un módulo con los datos introducidos")
    @PostMapping("/exercises")
    public ResponseEntity<Long> createEjercicio(@Parameter(description = "Datos para crear el nuevo ejercicio") @RequestBody EjercicioRequestDTO request) {
        return ResponseEntity.ok(cursoService.crearEjercicio(request.getNombre(), request.getTeoria(), request.getCodigoInicial(), request.getPuntos(), request.getEnunciado(), request.getLenguaje(), request.getIdModulo()));
    }

    @Operation(summary = "Añade tests a un ejercicio", description = "Guarda tests para un ejercicio existente")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/exercices/{idExercise}/tests")
    public ResponseEntity<?> createExerciseTests(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Identificador numérico del ejercicio") @PathVariable("idExercise") Long idExercise,
            @Parameter(description = "Lista con los tests a añadir") @RequestBody List<TestRequestDTO> tests) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(cursoService.crearTestsEjercicio(token, idExercise, tests));
    }

    @Operation(summary = "Obtener curso por ID", description = "Recupera la información de un curso específico a partir de su identificador")
    @GetMapping("/{id}/")
    public ResponseEntity<Curso> getCourse(
	@Parameter(description = "Identificador exclusivo del curso") @PathVariable("id") Long id) {
    	return ResponseEntity.ok(cursoService.getCurso(id));
    }

    @Operation(summary = "Obtener ejercicio por ID", description = "Recupera la información de un ejercicio específico a partir de su identificador")
    @GetMapping("exercises/{id}")
    public ResponseEntity<Ejercicio> getExercise(
	@Parameter(description = "Identificador del ejercicio a recuperar") @PathVariable("id") Long id) {
    	return ResponseEntity.ok(cursoService.getExercise(id));
    }

    @Operation(summary = "Obtiene los ejercicios del módulo al que pertenece un ejercicio",
               description = "Dado el ID de un ejercicio, devuelve la lista ordenada de ejercicios de su módulo")
    @GetMapping("exercises/{id}/module-exercises")
    public ResponseEntity<List<Ejercicio>> getModuleExercises(@Parameter(description = "Identificador base del ejercicio para ubicar al módulo") @PathVariable("id") Long id) {
        return ResponseEntity.ok(cursoService.getEjerciciosDelModulo(id));
    }

    @Operation(summary = "Obtener cursos", description = "Obtiene todos los cursos disponibles en la plataforma")
    @GetMapping("/")
    public ResponseEntity<List<Curso>> getCourses() {
    	return ResponseEntity.ok(cursoService.getAllCursos());
    }
    
    @Operation(summary = "Matricula al usuario en un curso", description = "Asocia al usuario autenticado como estudiante del curso indicado")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enrollCourse(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Identificador del curso a matricular") @PathVariable("id") Long cursoId) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        String token = authHeader.substring(7);
        cursoService.matricularUsuario(token, cursoId);
        return ResponseEntity.ok(new MessageResponseDto("Matriculado correctamente"));
    }

    @Operation(summary = "Obtiene los cursos en los que está matriculado el usuario", description = "Devuelve una lista de los cursos donde el usuario autenticado está inscrito")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/enrolled")
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
    public ResponseEntity<?> deleteCurso(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @Parameter(description = "Identificador del curso del profesor a eliminar") @RequestParam Long cursoId){
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
    public ResponseEntity<Integer> getTotalPuntos(@Parameter(description = "Identificador del curso de los puntos") @PathVariable("id") Long cursoId) {
        return ResponseEntity.ok(cursoService.getTotalPuntosByCurso(cursoId));
    }

    @Operation(summary = "Obtiene los puntos completados por el usuario en un curso", description = "Suma los puntos de los ejercicios que el usuario autenticado ha completado en el curso dado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/mis-puntos")
    public ResponseEntity<?> getMisPuntos(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Identificador del curso de los puntos") @PathVariable("id") Long cursoId) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        String token = authHeader.substring(7);
        return ResponseEntity.ok(cursoService.getPuntosCompletadosEnCurso(token, cursoId));
    }

    @Operation(summary = "Enviar mensaje al foro de un curso", description = "Permite enviar un mensaje nuevo al foro asociado a un curso específico")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/messages")
    public ResponseEntity<ForumMessageResponseDTO> sendMessage(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Identificador del curso objetivo") @PathVariable("id") Long cursoId,
            @Parameter(description = "Contenido y DTO del mensaje a enviar") @RequestBody MessageDTO mensaje) {
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

    @Operation(summary = "Obtener mensajes de un curso", description = "Recupera los mensajes del foro de un curso en el que el usuario está matriculado o es creador")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<ForumMessageResponseDTO>> getMessages(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
        @Parameter(description = "Identificador del curso de donde obtener los mensajes") @PathVariable("id") Long cursoId) {
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
    public ResponseEntity<CursoStatsDTO> getCourseStats(
            @PathVariable("id") Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
    public ResponseEntity<?> updateCurso(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Identificador del curso a actualizar") @PathVariable("id") Long cursoId,
            @Parameter(description = "DTO con los datos actualizados del curso y módulos") @RequestBody CursoUpdateDTO cursoUpdateDTO) {
        
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