package com.sergitxin.flexilearn.facade;

import com.sergitxin.flexilearn.dto.LoginRequestDto;
import com.sergitxin.flexilearn.dto.LoginResponseDto;
import com.sergitxin.flexilearn.dto.RegisterRequestDto;
import com.sergitxin.flexilearn.dto.LogoutRequestDto;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones relacionadas con el inicio de sesión y registro")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Crear cuenta", description = "Registra un nuevo usuario en el sistema")
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegisterRequestDto request) {
        try {
            authService.registrarUsuario(request.getNombre(), request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDto(e.getMessage()));
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token de acceso")
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequestDto request) {
        try {
            String token = authService.iniciarSesion(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new LoginResponseDto(token, "Inicio de sesión exitoso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto(e.getMessage()));
        }
    }

    @Operation(summary = "Cerrar sesión", description = "Invalida el token activo del usuario")
    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion(@RequestBody LogoutRequestDto request) {
        try {
            authService.cerrarSesion(request.getToken());
            return ResponseEntity.ok(new MessageResponseDto("Sesión finalizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto(e.getMessage()));
        }
    }
}
