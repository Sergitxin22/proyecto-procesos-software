package com.sergitxin.flexilearn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;


@Service
public class AdminService {
    private final UsuarioDao usuarioDao;
    private final CursoDAO cursoDao;
    
    public AdminService(UsuarioDao usuarioDao, CursoDAO cursoDao) {
        this.usuarioDao = usuarioDao;
        this.cursoDao = cursoDao;
    }

    public boolean eliminarUsuario(String token, String nombre){
    
        Usuario usuarioAdmin = usuarioDao.findByToken(token).get();
      
        if (usuarioAdmin.getEsAdmin()) {
         
            Usuario usuario = usuarioDao.findByNombre(nombre).get();
            cursoDao.deleteAll(usuario.getCursosCreados());
            usuarioDao.delete(usuario);
            return true;
        }
        return false;
    }

    public List<Usuario> getAllUsers() {
        return usuarioDao.findAll();
    }
}
