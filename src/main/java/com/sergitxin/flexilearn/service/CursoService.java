package com.sergitxin.flexilearn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.dto.CursoUpdateDTO;
import com.sergitxin.flexilearn.dto.CursoUpdateDTO.ModuloUpdateDTO;
import com.sergitxin.flexilearn.dto.CursoUpdateDTO.EjercicioUpdateDTO;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Usuario;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


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
    
    @Transactional
    public Curso actualizarCurso(String token, Long cursoId, CursoUpdateDTO cursoUpdateDTO) {
        // Verificar que el usuario es el dueño del curso
        Usuario user = usuarioDAO.findByToken(token).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Curso curso = cursoDAO.findById(cursoId).orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        if (!curso.getUsuario().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para editar este curso");
        }
        
        // Actualizar datos básicos del curso
        if (cursoUpdateDTO.getNombre() != null) {
            curso.setNombre(cursoUpdateDTO.getNombre());
        }
        if (cursoUpdateDTO.getCategoria() != null) {
            curso.setCategoria(cursoUpdateDTO.getCategoria());
        }
        if (cursoUpdateDTO.getDescripcion() != null) {
            curso.setDescripcion(cursoUpdateDTO.getDescripcion());
        }
        if (cursoUpdateDTO.getDificultad() != null) {
            curso.setDificultad(Dificultad.stringToDificultad(cursoUpdateDTO.getDificultad()));
        }
        
        // Actualizar módulos si se enviaron
        if (cursoUpdateDTO.getModulos() != null) {
            actualizarModulos(curso, cursoUpdateDTO.getModulos());
        }
        
        return cursoDAO.save(curso);
    }

    private void actualizarModulos(Curso curso, List<ModuloUpdateDTO> modulosDTO) {
        // Obtener la lista actual de módulos
        List<Modulo> modulosActuales = curso.getModulos();
        
        // Crear un mapa de módulos existentes por ID
        Map<Long, Modulo> modulosExistentes = modulosActuales.stream()
            .filter(m -> m.getId() != null)
            .collect(Collectors.toMap(Modulo::getId, m -> m));
        
        // Lista para los módulos que se mantendrán/actualizarán
        List<Modulo> modulosAMantener = new ArrayList<>();
        
        for (ModuloUpdateDTO moduloDTO : modulosDTO) {
            Modulo modulo;
            
            if (moduloDTO.getId() != null && modulosExistentes.containsKey(moduloDTO.getId())) {
                // Actualizar módulo existente
                modulo = modulosExistentes.get(moduloDTO.getId());
                if (moduloDTO.getNombre() != null) {
                    modulo.setNombre(moduloDTO.getNombre());
                }
                if (moduloDTO.getDescripcion() != null) {
                    modulo.setDescripcion(moduloDTO.getDescripcion());
                }
                modulosExistentes.remove(moduloDTO.getId());
            } else {
                // Crear nuevo módulo
                modulo = new Modulo();
                modulo.setCurso(curso);
                if (moduloDTO.getNombre() != null) {
                    modulo.setNombre(moduloDTO.getNombre());
                }
                if (moduloDTO.getDescripcion() != null) {
                    modulo.setDescripcion(moduloDTO.getDescripcion());
                }
            }
            
            // Actualizar ejercicios del módulo
            if (moduloDTO.getEjercicios() != null) {
                actualizarEjercicios(modulo, moduloDTO.getEjercicios());
            }
            
            modulosAMantener.add(modulo);
        }
        
        // Limpiar la lista actual y añadir los módulos actualizados
        // En lugar de reemplazar la referencia (curso.setModulos), modificamos el contenido
        modulosActuales.clear();
        modulosActuales.addAll(modulosAMantener);
    }

    private void actualizarEjercicios(Modulo modulo, List<EjercicioUpdateDTO> ejerciciosDTO) {
        // Obtener la lista actual de ejercicios
        List<Ejercicio> ejerciciosActuales = modulo.getEjercicios();
        
        // Crear un mapa de ejercicios existentes por ID
        Map<Long, Ejercicio> ejerciciosExistentes = ejerciciosActuales.stream()
            .filter(e -> e.getId() != null)
            .collect(Collectors.toMap(Ejercicio::getId, e -> e));
        
        // Lista para los ejercicios que se mantendrán/actualizarán
        List<Ejercicio> ejerciciosAMantener = new ArrayList<>();
        
        for (EjercicioUpdateDTO ejercicioDTO : ejerciciosDTO) {
            Ejercicio ejercicio;
            
            if (ejercicioDTO.getId() != null && ejerciciosExistentes.containsKey(ejercicioDTO.getId())) {
                // Actualizar ejercicio existente
                ejercicio = ejerciciosExistentes.get(ejercicioDTO.getId());
                if (ejercicioDTO.getNombre() != null) {
                    ejercicio.setNombre(ejercicioDTO.getNombre());
                }
                if (ejercicioDTO.getTeoria() != null) {
                    ejercicio.setTeoria(ejercicioDTO.getTeoria());
                }
                if (ejercicioDTO.getEnunciado() != null) {
                    ejercicio.setEnunciado(ejercicioDTO.getEnunciado());
                }
                if (ejercicioDTO.getCodigoInicial() != null) {
                    ejercicio.setCodigoInicial(ejercicioDTO.getCodigoInicial());
                }
                if (ejercicioDTO.getPuntos() > 0) {
                    ejercicio.setPuntos(ejercicioDTO.getPuntos());
                }
                if (ejercicioDTO.getLenguaje() != null) {
                    ejercicio.setLenguaje(ejercicioDTO.getLenguaje());
                }
                ejerciciosExistentes.remove(ejercicioDTO.getId());
            } else {
                // Crear nuevo ejercicio
                ejercicio = new Ejercicio();
                ejercicio.setModulo(modulo);
                if (ejercicioDTO.getNombre() != null) {
                    ejercicio.setNombre(ejercicioDTO.getNombre());
                }
                if (ejercicioDTO.getTeoria() != null) {
                    ejercicio.setTeoria(ejercicioDTO.getTeoria());
                }
                if (ejercicioDTO.getEnunciado() != null) {
                    ejercicio.setEnunciado(ejercicioDTO.getEnunciado());
                }
                if (ejercicioDTO.getCodigoInicial() != null) {
                    ejercicio.setCodigoInicial(ejercicioDTO.getCodigoInicial());
                }
                ejercicio.setPuntos(ejercicioDTO.getPuntos() > 0 ? ejercicioDTO.getPuntos() : 0);
                if (ejercicioDTO.getLenguaje() != null) {
                    ejercicio.setLenguaje(ejercicioDTO.getLenguaje());
                }
            }
            
            ejerciciosAMantener.add(ejercicio);
        }
        
        // Limpiar la lista actual y añadir los ejercicios actualizados
        // En lugar de reemplazar la referencia (modulo.setEjercicios), modificamos el contenido
        ejerciciosActuales.clear();
        ejerciciosActuales.addAll(ejerciciosAMantener);
    }
}