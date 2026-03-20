package com.sergitxin.flexilearn.service;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;

@Service
public class CursoService {

    private final CursoDAO cursoDAO;

    public CursoService(CursoDAO cursoDAO) {
        this.cursoDAO = cursoDAO;
    }

    public void crearCurso(String nombre, String categoria, String descripcion, Dificultad dificultad) {

        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setCategoria(categoria);
        curso.setDescripcion(descripcion);
        curso.setDificultad(dificultad);
        cursoDAO.save(curso);
    }
}
