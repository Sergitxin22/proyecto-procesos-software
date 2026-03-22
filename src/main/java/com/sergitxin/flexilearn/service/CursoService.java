package com.sergitxin.flexilearn.service;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Usuario;

@Service
public class CursoService {
    private final UsuarioDao usuarioDAO;
    private final CursoDAO cursoDAO;
    private final ModuloDAO moduloDAO;
    private final EjercicioDAO ejercicioDAO;

    public CursoService(UsuarioDao usuarioDAO, CursoDAO cursoDAO, ModuloDAO moduloDAO, EjercicioDAO ejercicioDAO) {
        this.usuarioDAO = usuarioDAO;
        this.cursoDAO = cursoDAO;
        this.moduloDAO = moduloDAO;
        this.ejercicioDAO = ejercicioDAO;
    }

    public Long crearCurso(String token, String nombre, String categoria, String descripcion, Dificultad dificultad) {
        Curso curso = new Curso();
        Usuario user = usuarioDAO.findByToken(token).get();
        curso.setUsuario(user);
        curso.setNombre(nombre);
        curso.setCategoria(categoria);
        curso.setDescripcion(descripcion);
        curso.setDificultad(dificultad);
        return cursoDAO.save(curso).getId();
    }

    public Long crearModulo(String nombre, String descripcion, Long idCurso) {
        Modulo modulo = new Modulo();
        modulo.setNombre(nombre);
        modulo.setDescripcion(descripcion);
        Curso curso = cursoDAO.findById(idCurso).get();
        modulo.setCurso(curso);
        curso.getModulos().add(modulo);
        cursoDAO.save(curso);
        return moduloDAO.save(modulo).getId();
    }

    public Long crearEjercicio(String nombre, String teoria, String codigoInicial, int puntos, String enunciado, String lenguaje, Long idModulo) {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setNombre(nombre);
        ejercicio.setTeoria(teoria);
        ejercicio.setCodigoInicial(codigoInicial);
        ejercicio.setPuntos(puntos);
        ejercicio.setEnunciado(enunciado);
        ejercicio.setLenguaje(lenguaje);
        Modulo modulo = moduloDAO.findById(idModulo).get();
        ejercicio.setModulo(modulo);
        modulo.getEjercicios().add(ejercicio);
        moduloDAO.save(modulo);
        return ejercicioDAO.save(ejercicio).getId();
    }

    public Curso getCurso(Long id) {
        return cursoDAO.findById(id).get();
    }
}
