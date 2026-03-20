package com.sergitxin.flexilearn.service;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Modulo;

@Service
public class CursoService {

    private final CursoDAO cursoDAO;
    private final ModuloDAO moduloDAO;

    public CursoService(CursoDAO cursoDAO, ModuloDAO moduloDAO) {
        this.cursoDAO = cursoDAO;
        this.moduloDAO = moduloDAO;
    }

    public Long crearCurso(String nombre, String categoria, String descripcion, Dificultad dificultad) {

        Curso curso = new Curso();
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
        cursoDAO.save(curso);
        return moduloDAO.save(modulo).getId();
    }
}
