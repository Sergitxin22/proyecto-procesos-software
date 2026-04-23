package com.sergitxin.flexilearn.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.DeleteRequestDTO;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
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

public class AdminController {
   
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
            this.adminService = adminService;
    }

    @Operation(summary = "Eliminar usuario", description = "Comprobar si es admin para poder eliminar usuarios")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/deleteUser")
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
}

