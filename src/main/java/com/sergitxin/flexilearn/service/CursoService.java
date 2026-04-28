package com.sergitxin.flexilearn.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.dao.TestDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Test;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.dto.TestRequestDTO;

@Service
public class CursoService {
    private final UsuarioDao usuarioDAO;
    private final CursoDAO cursoDAO;
    private final ModuloDAO moduloDAO;
    private final EjercicioDAO ejercicioDAO;
    private final TestDAO testDAO;

    public CursoService(UsuarioDao usuarioDAO, CursoDAO cursoDAO, ModuloDAO moduloDAO, EjercicioDAO ejercicioDAO, TestDAO testDAO) {
        this.usuarioDAO = usuarioDAO;
        this.cursoDAO = cursoDAO;
        this.moduloDAO = moduloDAO;
        this.ejercicioDAO = ejercicioDAO;
        this.testDAO = testDAO;
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

        Ejercicio saved = ejercicioDAO.save(ejercicio);

        return saved.getId();
    }

    public int crearTestsEjercicio(String token, Long idEjercicio, List<TestRequestDTO> testsRequest) {
        usuarioDAO.findByToken(token).orElseThrow();

        Ejercicio ejercicio = ejercicioDAO.findById(idEjercicio).orElseThrow();
        List<Test> tests = new ArrayList<>();

        if (testsRequest != null) {
            for (TestRequestDTO testRequest : testsRequest) {
                if (testRequest == null) {
                    continue;
                }

                String codigo = testRequest.getCodigo() == null ? "" : testRequest.getCodigo().trim();
                String salidaEsperada = testRequest.getSalidaEsperada() == null ? "" : testRequest.getSalidaEsperada().trim();
                if (codigo.isEmpty() && salidaEsperada.isEmpty()) {
                    continue;
                }

                Test test = new Test();
                test.setEjercicio(ejercicio);
                test.setCodigo(codigo);
                test.setSalidaEsperada(salidaEsperada);
                tests.add(test);
            }
        }

        if (!tests.isEmpty()) {
            testDAO.saveAll(tests);
        }
        return tests.size();
    }

    public Curso getCurso(Long id) {
        return cursoDAO.findById(id).get();
    }

    public Ejercicio getExercise(Long id) {
        return ejercicioDAO.findById(id).get();
    }

    public List<Curso> getAllCursos() {
        return cursoDAO.findAll();
    }
    
    public void matricularUsuario(String token, Long cursoId) {
        Usuario user = usuarioDAO.findByToken(token).get();
        Curso curso = cursoDAO.findById(cursoId).get();
        if (!user.getCursosMatriculados().contains(curso)) {
            user.getCursosMatriculados().add(curso);
            usuarioDAO.save(user);
        }
    }

    public List<Curso> getCursosMatriculados(String token) {
        Usuario user = usuarioDAO.findByToken(token).get();
        return user.getCursosMatriculados();
    }

    public boolean eliminarCursoDelProfesor(String token, Long cursoId) {
        Usuario user = usuarioDAO.findByToken(token).get();
        Curso curso = cursoDAO.findById(cursoId).get();
            
            // Verificar que el profesor es el dueño del curso
        if (curso.getUsuario().getId().equals(user.getId())) {
            cursoDAO.deleteById(cursoId);
            return true;
        }

        return false;
    }
}
