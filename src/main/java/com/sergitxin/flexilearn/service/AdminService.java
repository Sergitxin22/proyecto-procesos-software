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
    
    /**
     * Construye e inyecta las dependencias necesarias.
     * @param usuarioDao Persistencia en la tabla de control de usuarios.
     * @param cursoDao Persistencia en la tabla de los cursos creados.
     */
    public AdminService(UsuarioDao usuarioDao, CursoDAO cursoDao) {
        this.usuarioDao = usuarioDao;
        this.cursoDao = cursoDao;
    }

    /**
     * Autoriza la purga del usuario indicado siempre que sea invocado por permisos de control superior (administrador).
     * @param token Criptograma autorizante (administrador).
     * @param nombre Alias o login referenciado en el sistema al que proceder a borrar permanentemente y sus dependencias.
     * @return Devuelve un true lógico corroborando la eliminación; caso contrario false indicando carencia de atribuciones o fallos.
     */
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

    /**
     * Enumera una colección general completa de perfiles y usuarios.
     * @return ArrayList que retorna listados base tipo entidad (Usuario).
     */
    public List<Usuario> getAllUsers() {
        return usuarioDao.findAll();
    }

    /**
     * Mide e ingresa a una cuenta resumida calculando las interacciones activas y puntajes totales conseguidos durante cada avance individual por la comunidad.
     * @param token Cadena de acceso criptográfica validando la capacidad del emisor para requerir estos informes internos.
     * @return Una colección convertida temporalizada a una serie estadistica para los administradores o un iterativo Null de denegaciones.
     */
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

    /**
     * Fuerzaborra un nivel de curso académico y todos los contenidos, módulos relativos mediante la aprobación de las credenciales de administración superpuestas a un docente.
     * @param token Cadena con los sellos del admin logueado en la actualidad global.
     * @param cursoId Identificativo en forma Long de la clase afectada.
     * @return El control de supresión retornando un positivo lógico si borró las relaciones o lo desmiente (false) ante ausencias o falta pericial.
     */
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
