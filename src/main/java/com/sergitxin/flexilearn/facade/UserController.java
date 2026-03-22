package com.sergitxin.flexilearn.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergitxin.flexilearn.dto.GetUserInfoRequestDto;
import com.sergitxin.flexilearn.dto.LoginRequestDto;
import com.sergitxin.flexilearn.dto.LoginResponseDto;
import com.sergitxin.flexilearn.dto.LogoutRequestDto;
import com.sergitxin.flexilearn.dto.MessageResponseDto;
import com.sergitxin.flexilearn.dto.RegisterRequestDto;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.service.AuthService;
import com.sergitxin.flexilearn.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/createdCourses")
    public ResponseEntity<java.util.List<Curso>> getAllUsers(@RequestBody GetUserInfoRequestDto request) {
        return ResponseEntity.ok(userService.obtenerCursosdeUsuario(request.getToken()));
    }
}
