package com.sergitxin.flexilearn.service;

import java.util.List;
import java.util.Date;
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
import com.sergitxin.flexilearn.entity.Mensaje;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Test;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.dto.TestRequestDTO;
import com.sergitxin.flexilearn.dto.CursoStatsDTO;
import com.sergitxin.flexilearn.dto.AlumnoProgresoDTO;

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

    public List<Ejercicio> getEjerciciosDelModulo(Long ejercicioId) {
        Ejercicio ejercicio = ejercicioDAO.findById(ejercicioId).orElseThrow();
        Modulo modulo = ejercicio.getModulo();
        if (modulo == null) return List.of(ejercicio);
        return moduloDAO.findById(modulo.getId()).orElseThrow().getEjercicios();
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

    public Mensaje guardarMensaje(String token, String texto, Long cursoId) {
        Usuario user = usuarioDAO.findByToken(token).get();
        Curso curso = cursoDAO.findById(cursoId).get();
        if ((user.getCursosMatriculados().contains(curso) || user.getCursosCreados().contains(curso)) == false) {
            return null;
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setCurso(curso);
        mensaje.setFecha(new Date());
        mensaje.setTexto(texto);
        mensaje.setUsuario(user);
        
        curso.getMensajes().add(mensaje);
        cursoDAO.save(curso);
        return mensaje;
    }

    public List<Mensaje> getMessages(Long cursoId, String token) {
        Usuario user = usuarioDAO.findByToken(token).get();
        Curso curso = cursoDAO.findById(cursoId).get();
        if ((user.getCursosMatriculados().contains(curso) || user.getCursosCreados().contains(curso)) == false) {
            return new ArrayList<>();
        }
        return curso.getMensajes();
    }

    public int getPuntosCompletadosEnCurso(Usuario usuario, Curso curso) {
        return usuario.getEjerciciosCompletados().stream()
                .filter(e -> e.getModulo() != null
                        && e.getModulo().getCurso() != null
                        && e.getModulo().getCurso().getId().equals(curso.getId()))
                .mapToInt(Ejercicio::getPuntos)
                .sum();
    }

    public int getPuntosCompletadosEnCurso(String token, Long cursoId) {
        Usuario usuario = usuarioDAO.findByToken(token).orElseThrow();
        Curso curso = cursoDAO.findById(cursoId).orElseThrow();
        return getPuntosCompletadosEnCurso(usuario, curso);
    }

    public int getTotalPuntosByCurso(Long cursoId) {
        cursoDAO.findById(cursoId).orElseThrow();
        return cursoDAO.getTotalPuntosByCursoId(cursoId);
    }

    public CursoStatsDTO getCourseStats(String token, Long cursoId) {
        Usuario profesor = usuarioDAO.findByToken(token).orElseThrow();
        Curso curso = cursoDAO.findById(cursoId).orElseThrow();

        if (!curso.getUsuario().getId().equals(profesor.getId()) && !profesor.getEsAdmin()) {
            return null; // Return null if not authorized
        }

        CursoStatsDTO stats = new CursoStatsDTO();
        
        List<Usuario> matriculados = curso.getUsuariosMatriculados();
        stats.setTotalAlumnos(matriculados.size());
        
        List<Ejercicio> ejerciciosCurso = new ArrayList<>();
        for (Modulo m : curso.getModulos()) {
            ejerciciosCurso.addAll(m.getEjercicios());
        }
        stats.setTotalEjercicios(ejerciciosCurso.size());

        List<AlumnoProgresoDTO> progresos = new ArrayList<>();
        for (Usuario alumno : matriculados) {
            AlumnoProgresoDTO progreso = new AlumnoProgresoDTO();
            progreso.setId(alumno.getId());
            progreso.setNombre(alumno.getNombre());
            progreso.setEmail(alumno.getEmail());

            int completados = 0;
            int puntos = 0;
            for (Ejercicio ej : alumno.getEjerciciosCompletados()) {
                if (ej.getModulo() != null && ej.getModulo().getCurso() != null && ej.getModulo().getCurso().getId().equals(curso.getId())) {
                    completados++;
                    puntos += ej.getPuntos();
                }
            }
            progreso.setEjerciciosCompletados(completados);
            progreso.setPuntosTotales(puntos);
            progresos.add(progreso);
        }
        stats.setProgresoAlumnos(progresos);

        return stats;
    }

}
