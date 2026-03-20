package com.sergitxin.flexilearn.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.CursoRequestDTO;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/courses")
    public ResponseEntity<Void> createCurso(@RequestBody CursoRequestDTO request) {
        cursoService.crearCurso(request.getNombre(), request.getCategoria(), request.getDescripcion(), Dificultad.stringToDificultad(request.getDificultad()));
        return ResponseEntity.ok().build();
    }
}
