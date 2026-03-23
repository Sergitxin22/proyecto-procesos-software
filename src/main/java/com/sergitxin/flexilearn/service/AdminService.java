package com.sergitxin.flexilearn.service;

import org.springframework.stereotype.Service;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;


@Service
public class AdminService {
    private final UsuarioDao usuarioDao;
    
    public AdminService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean eliminarUsuario(String token, String nombre){
        Usuario usuarioAdmin = usuarioDao.findByToken(token).get();
        if (usuarioAdmin.getEsAdmin()) {
            Usuario usuario = usuarioDao.findByNombre(nombre).get();
            usuarioDao.delete(usuario);
            return true;
        }
        return false;
    }
}
