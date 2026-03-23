package com.sergitxin.flexilearn.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con obtener datos de los usuarios de la plataforma")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener todos los cursos creados por el usuario", description = "Recupera la lista de todos los cursos creados por el usuario")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/createdCourses")
    public ResponseEntity<?> getAllUsers(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
            }
            
            String token = authHeader.substring(7);
        return ResponseEntity.ok(userService.obtenerCursosdeUsuario(token)); // Posible mejora sanitizando el response
    }
}
