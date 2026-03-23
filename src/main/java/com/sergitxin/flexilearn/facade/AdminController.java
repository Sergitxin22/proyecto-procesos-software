package com.sergitxin.flexilearn.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.DeleteRequestDTO;
import com.sergitxin.flexilearn.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    @PostMapping("/deleteUsers")
    public ResponseEntity<Integer> deleteUser(@RequestBody DeleteRequestDTO request){
       boolean eliminado = adminService.eliminarUsuario(request.getToken(), request.getNombreUsuario());
       if (eliminado) {
        return ResponseEntity.ok(1);
       }
       return ResponseEntity.ok(0); 
    }
}


