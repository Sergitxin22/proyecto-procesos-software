package com.sergitxin.flexilearn.service;

import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;

import org.springframework.stereotype.Service;

import java.util.List;

import com.sergitxin.flexilearn.entity.Curso;

@Service
public class UserService {
    
    private final UsuarioDao usuarioDao;

    /**
     * Componente central del control lógico individual. Genera inyección sobre DAOs para los Usuarios.
     * @param usuarioDao Utilidad interface conectora hacia la BB.DD.
     */
    public UserService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    /**
     * Localiza un profesor u usuario creador para destapar en un resumen secuencial cuáles materias se originaron por parte de su perfil activo.
     * @param token Expresión textual de autenticación Bearer de autorización personal.
     * @return Una colección en List de las entidades Curso publicadas por medio de la sesión provista.
     */
    public List<Curso> obtenerCursosdeUsuario(String token) {
        Usuario usuario = usuarioDao.findByToken(token).get();
        return usuario.getCursosCreados();
    }
}
