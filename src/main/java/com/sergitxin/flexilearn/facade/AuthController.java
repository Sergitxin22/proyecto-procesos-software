package com.sergitxin.flexilearn.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.LoginRequestDto;
import com.sergitxin.flexilearn.dto.LoginResponseDto;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.dto.RegisterRequestDto;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Operaciones relacionadas con el inicio de sesión y registro")
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
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
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
            }
            
            String token = authHeader.substring(7);
            authService.cerrarSesion(token);
            return ResponseEntity.ok(new MessageResponseDto("Sesión finalizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto(e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener usuario por token (comentar en produción)", description = "Recupera la información del usuario asociado al token proporcionado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    public ResponseEntity<?> getUsuarioByToken(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
		try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
            }
            
            String token = authHeader.substring(7);
			Usuario usuario = authService.obtenerUsuarioByToken(token);
			return ResponseEntity.ok(usuario);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto(e.getMessage()));
		}
	}

    @Operation(summary = "Obtener todos los usuarios (comentar en produción)", description = "Recupera la lista de todos los usuarios registrados")
    @GetMapping("/users")
    public ResponseEntity<java.util.List<Usuario>> getAllUsers() {
        return ResponseEntity.ok(authService.obtenerTodosLosUsuarios());
    }
}
