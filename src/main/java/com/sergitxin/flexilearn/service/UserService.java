package com.sergitxin.flexilearn.service;

import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.external.AuthExternalFactory;
import com.sergitxin.flexilearn.external.AuthExternalPort;
import com.sergitxin.flexilearn.external.AuthProvider;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sergitxin.flexilearn.entity.Curso;

@Service
public class UserService {
    
    private final UsuarioDao usuarioDao;

    public UserService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public List<Curso> obtenerCursosdeUsuario(String token) {
        Usuario usuario = usuarioDao.findByToken(token).get();
        return usuario.getCursosCreados();
    }
}
