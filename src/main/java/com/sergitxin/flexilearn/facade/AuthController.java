package com.sergitxin.flexilearn.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
/**
 * Controlador REST para la autenticación de usuarios.
 * Expone endpoints para el registro y login.
 */
public class AuthController {

    private final AuthService authService;

    /**
     * Instancia el controlador de autenticación proporcionando el servicio necesario.
     * @param authService Servicio principal para la lógica de autenticación y gestión de usuarios.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Crear cuenta", description = "Registra un nuevo usuario en el sistema")
    @PostMapping("/registro")
    /**
     * Registra un nuevo usuario en la base de datos a partir de los datos recibidos.
     * @param request Objeto JSON con el nombre, correo electrónico y contraseña del usuario.
     * @return 201 (CREATED) si el registro fue exitoso, o 400 (BAD REQUEST) si hubo un error.
     */
    public ResponseEntity<?> registrar(@Parameter(description = "Datos de registro del usuario (nombre, email y contraseña)") @RequestBody RegisterRequestDto request) {
        try {
            authService.registrarUsuario(request.getNombre(), request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDto(e.getMessage()));
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token de acceso")
    @PostMapping("/login")
    /**
     * Autentica a un usuario y le proporciona un token JWT de acceso.
     * @param request Objeto JSON con el correo electrónico y la contraseña del usuario.
     * @return 200 (OK) con el token si las credenciales son válidas, o 401 (UNAUTHORIZED) en caso contrario.
     */
    public ResponseEntity<?> iniciarSesion(@Parameter(description = "Credenciales del usuario (email y contraseña)") @RequestBody LoginRequestDto request) {
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
    /**
     * Invalida el token de acceso actual del usuario registrado.
     * @param authHeader El encabezado de autorización que contiene el token JWT.
     * @return 200 (OK) con un mensaje de éxito, o 401 si no hay token o es inválido.
     */
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

    @Operation(summary = "Eliminar cuenta", description = "Elimina la cuenta del usuario ")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete")
    /**
     * Elimina permanentemente la cuenta del usuario autenticado de la base de datos.
     * @param authHeader El encabezado de autorización que contiene el token JWT validado del solicitante.
     * @return 200 (OK) si la operación fue exitosa, o código HTTP correspondiente a falta de autorización o no encontrado.
     */
    public ResponseEntity<?> eliminarCuenta(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
            }
            
            String token = authHeader.substring(7);
            try {
                authService.eliminarCuenta(token);
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto(e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener usuario por token (comentar en produción)", description = "Recupera la información del usuario asociado al token proporcionado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    /**
     * Recupera y devuelve el perfil del usuario utilizando su token actual activo.
     * @param authHeader El encabezado de autorización HTTP con el Bearer token correspondiente.
     * @return 200 (OK) con la información del usuario en el cuerpo o 401 si no hay coincidencia.
     */
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
    /**
     * Consulta y extrae el listado con todos los usuarios registrados. Principalmente para pruebas.
     * @return Respuesta HTTP (OK) con una lista de todos los objetos Usuario registrados.
     */
    public ResponseEntity<java.util.List<Usuario>> getAllUsers() {
        return ResponseEntity.ok(authService.obtenerTodosLosUsuarios());
    }
}
