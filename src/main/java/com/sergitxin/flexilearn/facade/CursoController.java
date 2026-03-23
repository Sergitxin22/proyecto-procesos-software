package com.sergitxin.flexilearn.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.CursoRequestDTO;
import com.sergitxin.flexilearn.dto.EjercicioRequestDTO;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.dto.ModuloRequestDTO;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
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
    public ResponseEntity<?> createCurso(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody CursoRequestDTO request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
            
        String token = authHeader.substring(7);

        return ResponseEntity.ok(cursoService.crearCurso(token, request.getNombre(), request.getCategoria(), request.getDescripcion(), Dificultad.stringToDificultad(request.getDificultad())));
    }

    @Operation(summary = "Añade un módulo a un curso", description = "Añade en la base de datos un módulo a un curso con los datos introducidos")
    @PostMapping("/modules")
    public ResponseEntity<Long> createModulo(@RequestBody ModuloRequestDTO request) {
        return ResponseEntity.ok(cursoService.crearModulo(request.getNombre(), request.getDescripcion(), request.getIdCurso()));
    }

    @Operation(summary = "Añade un ejercicio a un módulo", description = "Añade en la base de datos un ejercicio a un módulo con los datos introducidos")
    @PostMapping("/exercises")
    public ResponseEntity<Long> createEjercicio(@RequestBody EjercicioRequestDTO request) {
        return ResponseEntity.ok(cursoService.crearEjercicio(request.getNombre(), request.getTeoria(), request.getCodigoInicial(), request.getPuntos(), request.getEnunciado(), request.getLenguaje(), request.getIdModulo()));
    }

    @GetMapping("/{id}/")
    public ResponseEntity<Curso> getCourse(
    @Parameter(name = "id", description = "El identificador único del contenedor a cambiar", required = true)
	@PathVariable("id") Long id) {
    	return ResponseEntity.ok(cursoService.getCurso(id));
    }
}
