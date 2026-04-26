package com.sergitxin.flexilearn.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.SolucionDTO;
import com.sergitxin.flexilearn.service.ExerciseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "*")
@Tag(name = "Ejercicios", description = "Operaciones relacionadas con verificar soluciones")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }
 
    @Operation(summary = "Verificar ejercicio", description = "Comprueba si la solución recibida es correcta")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyExercise(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody SolucionDTO request) {
        String token = authHeader.substring(7);
    	boolean result = exerciseService.verifyExercise(request.getIdEjercicio(), request.getCodigo(), token);
        return ResponseEntity.ok(result);
    }
}
