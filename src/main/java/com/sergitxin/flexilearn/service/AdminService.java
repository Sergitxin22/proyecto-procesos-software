package com.sergitxin.flexilearn.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.dto.UsuarioActivityStatsDTO;
import com.sergitxin.flexilearn.entity.Ejercicio;
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

    public List<UsuarioActivityStatsDTO> obtenerActividadUsuarios(String token) {
        Usuario usuarioAdmin = usuarioDao.findByToken(token).get();
        if (!usuarioAdmin.getEsAdmin()) {
            return null;
        }
        List<Usuario> usuarios = usuarioDao.findAll();
        return usuarios.stream().map(u -> {
            int puntosAcumulados = u.getEjerciciosCompletados().stream()
                    .mapToInt(Ejercicio::getPuntos)
                    .sum();
            return new UsuarioActivityStatsDTO(
                    u.getId(),
                    u.getNombre(),
                    u.getEmail(),
                    u.getEsAdmin(),
                    u.getCursosCreados() != null ? u.getCursosCreados().size() : 0,
                    u.getCursosMatriculados() != null ? u.getCursosMatriculados().size() : 0,
                    u.getEjerciciosCompletados().size(),
                    puntosAcumulados);
        }).collect(Collectors.toList());
    }

    public boolean eliminarCurso(String token, Long cursoId) {
        
        Usuario usuarioAdmin = usuarioDao.findByToken(token).get();
            
        if (usuarioAdmin.getEsAdmin()) {

            if (cursoDao.existsById(cursoId)) {
            cursoDao.deleteById(cursoId);
            return true;
            }

        }
        return false;
    }
}
