package com.sergitxin.flexilearn.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.DeleteRequestDTO;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.dto.UsuarioActivityStatsDTO;
import com.sergitxin.flexilearn.dto.UsuarioDTO;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admins", description = "Operaciones relacionadas con las operaciones de administrador")
/**
 * Controlador REST para las utilidades de administración de la plataforma.
 * Permite a los usuarios con rol de administrador eliminar usuarios, gestionar
 * cursos de forma global y obtener estadísticas detalladas sobre el uso del sitio.
 */
public class AdminController {
   
    private final AdminService adminService;

    /**
     * Instancia el controlador de administrador y proporciona el servicio de negocio.
     * @param adminService Servicio principal para las funciones de administración.
     */
    public AdminController(AdminService adminService) {
            this.adminService = adminService;
    }

    @Operation(summary = "Eliminar usuario", description = "Comprobar si es admin para poder eliminar usuarios")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/deleteUser")
    /**
     * Elimina a un usuario de la base de datos de la plataforma en base a su nombre de usuario.
     * @param authHeader Token Bearer de la sesión actual de administración.
     * @param request Objeto que encapsula el nombre de usuario a eliminar.
     * @return 1 si ha sido eliminado satisfactoriamente, o 0 en caso contrario. Además de mensajes HTTP de error.
     */
    public ResponseEntity<?> deleteUser(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody DeleteRequestDTO request){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
            
        String token = authHeader.substring(7);
        
       boolean eliminado = adminService.eliminarUsuario(token, request.getNombreUsuario());
       if (eliminado) {
        return ResponseEntity.ok(1);
       }
       return ResponseEntity.ok(0); 
    }

    @Operation(summary = "Usuarios", description = "Obtiene todos los usuarios")
    @GetMapping("/users")
    /**
     * Consulta y devuelve la lista completa de usuarios registrados en el sistema, convertidos en su representación DTO.
     * @return Una respuesta HTTP OK (200) que contiene la colección de usuariosDTO.
     */
    public ResponseEntity<List<UsuarioDTO>> getAllUsers(){
        List<Usuario> usuarios = adminService.getAllUsers();
        List<UsuarioDTO> usuariosDTO = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setEmail(usuario.getEmail());
            usuarioDTO.setEsAdmin(usuario.getEsAdmin());
            usuarioDTO.setId(usuario.getId());
            usuarioDTO.setNombre(usuario.getNombre());
            usuariosDTO.add(usuarioDTO);
        }
        return ResponseEntity.ok(usuariosDTO);
    }

    @Operation(summary = "Eliminar curso", description = "Eliminar un curso para que estudiantes y profesores dejen de tener acceso")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/deleteCurso")
    /**
     * Permite a un administrador forzar la eliminación de un curso por su ID y su contenido subyacente de la plataforma.
     * @param authHeader Token de autorización del administrador.
     * @param cursoId Identificador numérico local del curso en la base de datos a borrar.
     * @return Código de éxito 1 si ha sido un borrado exitoso, de lo contrario 0.
     */
    public ResponseEntity<?> deleteCurso(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestParam Long cursoId){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }
            
        String token = authHeader.substring(7);
        
        boolean eliminado = adminService.eliminarCurso(token, cursoId);
        if (eliminado) {
        return ResponseEntity.ok(1);
        }
        return ResponseEntity.ok(0); 
    }

    @Operation(summary = "Actividad de usuarios", description = "Obtiene estadísticas de actividad por usuario: cursos creados, matriculaciones y ejercicios completados")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/usersStats")
    /**
     * Recopila un informe estadístico sobre la actividad de todos los usuarios dentro de la plataforma (cursos, matriculas, ejercicios).
     * @param authHeader Token de seguridad Bearer que debe pertenecer a un usuario administrador.
     * @return 200 (OK) con la lista estadística agregada o un código no autorizado si el token es falso o no corresponde a admin.
     */
    public ResponseEntity<?> getUsersActivityStats(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto("Token no proporcionado o inválido"));
        }

        String token = authHeader.substring(7);
        List<UsuarioActivityStatsDTO> usersStats = adminService.obtenerActividadUsuarios(token);
        if (usersStats == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponseDto("Acceso denegado: se requieren permisos de administrador"));
        }
        return ResponseEntity.ok(usersStats);
    }
}

