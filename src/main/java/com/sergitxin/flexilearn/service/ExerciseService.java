package com.sergitxin.flexilearn.service;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Test;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.external.PistonGateway;

@Service
public class ExerciseService {
    private final EjercicioDAO ejercicioDAO;
    private final UsuarioDao usuarioDao;
    private final PistonGateway pistonGateway;

    public ExerciseService(EjercicioDAO ejercicioDAO, PistonGateway pistonGateway, UsuarioDao usuarioDao) {
        this.ejercicioDAO = ejercicioDAO;
        this.pistonGateway = pistonGateway;
        this.usuarioDao = usuarioDao;
    }

    public boolean verifyExercise(Long idEjercicio, String codigo, String token) {
        Ejercicio ejercicio = ejercicioDAO.getReferenceById(idEjercicio);

        boolean solved = true;
        for (Test test : ejercicio.getTests()) {
            String output = pistonGateway.execute(codigo);
            System.out.println(test.getSalidaEsperada());
            if (!test.getSalidaEsperada().equals(output)) {
                solved = false;
            }
        }

        if (solved) {
            Usuario usuario = usuarioDao.findByToken(token).get();
            if (!usuario.getEjerciciosCompletados().contains(ejercicio)) {
                usuario.getEjerciciosCompletados().add(ejercicio);
                usuarioDao.save(usuario);
            }

        }

        return solved;
    }
}
