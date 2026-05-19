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

    /**
     * Construye un servicio encargado de las pruebas o control sobre ejercicios.
     * @param ejercicioDAO Manipulación del componente tabla ejercicios en BD.
     * @param pistonGateway Motor externo o sistema de ejecución remota Piston.
     * @param usuarioDao Conexion CRUD de gestión y perfil de usuario general.
     */
    public ExerciseService(EjercicioDAO ejercicioDAO, PistonGateway pistonGateway, UsuarioDao usuarioDao) {
        this.ejercicioDAO = ejercicioDAO;
        this.pistonGateway = pistonGateway;
        this.usuarioDao = usuarioDao;
    }

    /**
     * Determina la idoneidad funcional de una solución codificada contra un grupo de Tests Piston preinscritos.
     * En caso de resolverla positivamente se añadirá al progreso estudiado del usuario con su respectiva nota o premio en puntos.
     * @param idEjercicio Elemento original extraido que se va a tratar de sobrepasar.
     * @param codigo Componente sintáctico bruto entregado por el participante para evaluarse en sandbox.
     * @param token Cadena alfanumérica JWT comprobatorio del usuario participante al que sumarle la puntuación si tiene éxito.
     * @return Logicamente true tras aprobar cada salida esperada; si se altera un resultado será false.
     */
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
