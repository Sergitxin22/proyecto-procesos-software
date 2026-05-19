package com.sergitxin.flexilearn.service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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
import com.sergitxin.flexilearn.dto.CursoUpdateDTO;
import com.sergitxin.flexilearn.dto.CursoUpdateDTO.ModuloUpdateDTO;
import com.sergitxin.flexilearn.dto.CursoUpdateDTO.EjercicioUpdateDTO;

@Service
/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los cursos, 
 * módulos, ejercicios, tests y estadísticas asociadas a los mismos.
 */
public class CursoService {
    private final UsuarioDao usuarioDAO;
    private final CursoDAO cursoDAO;
    private final ModuloDAO moduloDAO;
    private final EjercicioDAO ejercicioDAO;
    private final TestDAO testDAO;

    /**
     * Constructor del servicio.
     * @param usuarioDAO Acceso a datos de los usuarios.
     * @param cursoDAO Acceso a datos de los cursos.
     * @param moduloDAO Acceso a datos de los módulos.
     * @param ejercicioDAO Acceso a datos de los ejercicios.
     * @param testDAO Acceso a datos de los tests.
     */
    public CursoService(UsuarioDao usuarioDAO, CursoDAO cursoDAO, ModuloDAO moduloDAO, EjercicioDAO ejercicioDAO, TestDAO testDAO) {
        this.usuarioDAO = usuarioDAO;
        this.cursoDAO = cursoDAO;
        this.moduloDAO = moduloDAO;
        this.ejercicioDAO = ejercicioDAO;
        this.testDAO = testDAO;
    }

    /**
     * Crea un nuevo curso y lo asocia al usuario autenticado por su token.
     * 
     * @param token Token de seguridad del usuario que crea el curso.
     * @param nombre Nombre del curso.
     * @param categoria Categoría a la que pertenece el curso.
     * @param descripcion Descripción detallada del curso.
     * @param dificultad Grado de dificultad del curso.
     * @return El identificador (ID) del curso recién creado.
     */
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

    /**
     * Crea un módulo asociado a un curso.
     * @param nombre Nombre del módulo.
     * @param descripcion Descripción del módulo.
     * @param idCurso ID del curso padre.
     * @return ID del módulo creado.
     */
    public Long crearModulo(String nombre, String descripcion, Long idCurso) {
        Modulo modulo = new Modulo();
        modulo.setNombre(nombre);
        modulo.setDescripcion(descripcion);
        Curso curso = cursoDAO.findById(idCurso).get();
        modulo.setCurso(curso);
        
        return moduloDAO.save(modulo).getId();
    }

    /**
     * Crea un ejercicio dentro de un módulo.
     * @param nombre Nombre del ejercicio.
     * @param teoria Teoría del ejercicio.
     * @param codigoInicial Código inicial.
     * @param puntos Puntos que otorga.
     * @param enunciado Enunciado del problema.
     * @param lenguaje Lenguaje de programación.
     * @param idModulo ID del módulo.
     * @return ID del ejercicio.
     */
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

    /**
     * Añade tests a un ejercicio.
     * @param token Token del profesor.
     * @param idEjercicio ID del ejercicio.
     * @param testsRequest Lista de tests.
     * @return Número de tests creados.
     */
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

    /**
     * Consulta detallada del perfil o estructura completa de un único curso.
     * @param id Referencia original del identificador en Base de Datos para dicho curso.
     * @return Curso encontrado y obtenido.
     */
    public Curso getCurso(Long id) {
        return cursoDAO.findById(id).get();
    }

    /**
     * Obten una instancia singular sobre un ejercicio solicitado.
     * @param id ID interno mapeado con la tabla para encontrar al ejercicio.
     * @return Ejercicio de BD persistido.
     */
    public Ejercicio getExercise(Long id) {
        return ejercicioDAO.findById(id).get();
    }

    /**
     * Trae y ordena los ejercicios complementarios o compañeros registrados dentro de la misma categoría de un módulo concreto.
     * @param ejercicioId Id de cualquier ejercicio residente en ese modulo que sirva para hallar a sus hermanos.
     * @return Formato tipado Lista con Ejercicios que habitan en la misma unidad temática.
     */
    public List<Ejercicio> getEjerciciosDelModulo(Long ejercicioId) {
        Ejercicio ejercicio = ejercicioDAO.findById(ejercicioId).orElseThrow();
        Modulo modulo = ejercicio.getModulo();
        if (modulo == null) return List.of(ejercicio);
        return moduloDAO.findById(modulo.getId()).orElseThrow().getEjercicios();
    }

    /**
     * Pide una colección sin filtros con todas las entidades mapeadas en forma de Curso disponibles en red local.
     * @return Lista genérica y completa con cada Curso.
     */
    public List<Curso> getAllCursos() {
        return cursoDAO.findAll();
    }
    
    /**
     * Vincula y añade un estudiante emisor a un plan educativo de un curso, integrándolo si antes no estaba.
     * @param token Codigo UUID JWT confirmatorio.
     * @param cursoId Identitficador referencial del curso a asimilar.
     */
    public void matricularUsuario(String token, Long cursoId) {
        Usuario user = usuarioDAO.findByToken(token).get();
        Curso curso = cursoDAO.findById(cursoId).get();
        if (!user.getCursosMatriculados().contains(curso)) {
            user.getCursosMatriculados().add(curso);
            usuarioDAO.save(user);
        }
    }

    /**
     * Entrega las asignaturas y cursos aceptados o integrados con este estudiante en base de su token de progreso activo.
     * @param token Cadena generada válida del alumno referenciado.
     * @return List de sus entidades Curso en donde ha materializado previas suscripciones.
     */
    public List<Curso> getCursosMatriculados(String token) {
        Usuario user = usuarioDAO.findByToken(token).get();
        return user.getCursosMatriculados();
    }

    /**
     * Valida permisos y expulsa totalmente a un curso creado por quien lanza esta petición del sistema general.
     * @param token Identificativo JWT perteneciente al profesor creador.
     * @param cursoId Referencia base de tabla del curso.
     * @return Una respuesta True en modo Booleano indicando el borrado efectivo final, o False si él no figurase como dueño originario.
     */
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

    /**
     * Instancia un subregistro representativo para una publicación comunitaria o un mensaje sobre la tabla del foro vinculado de un curso.
     * @param token Codigo UUID garante del emisor de esta consulta.
     * @param texto Cadenas de caracteres conteniendo literal de cuerpo textual publicable.
     * @param cursoId Lugar receptor donde agrupar e impactar la respuesta de mensaje final.
     * @return Transacción Mensaje ya procesada en un modelo DB instanciado con su ID, etc.
     */
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

    /**
     * Trae y ordena cronológicamente o por origen la hilera completa de mensajes expuestos para un ambiente escolar.
     * @param cursoId Modulo identificador general para recuperar a sus entidades subordinadas en foro.
     * @param token Identificación local probatoria del interrogante con posibilidad de vista.
     * @return Archivo en colección List con representaciones tipadas Mensaje listas.
     */
    public List<Mensaje> getMessages(Long cursoId, String token) {
        Usuario user = usuarioDAO.findByToken(token).get();
        Curso curso = cursoDAO.findById(cursoId).get();
        if ((user.getCursosMatriculados().contains(curso) || user.getCursosCreados().contains(curso)) == false) {
            return new ArrayList<>();
        }
        return curso.getMensajes();
    }

    /**
     * Computa en memoria la cantidad ponderada o nota final basada en los ejercicios de las resoluciones ganadoras.
     * @param usuario Representación real recuperada de Usuario.
     * @param curso Material representativo del que examinar todos esos ejercicios pertenecientes.
     * @return El porcentaje o Integer numérico entero sumado de la experiencia obtenida.
     */
    public int getPuntosCompletadosEnCurso(Usuario usuario, Curso curso) {
        return usuario.getEjerciciosCompletados().stream()
                .filter(e -> e.getModulo() != null
                        && e.getModulo().getCurso() != null
                        && e.getModulo().getCurso().getId().equals(curso.getId()))
                .mapToInt(Ejercicio::getPuntos)
                .sum();
    }

    /**
     * Calcula la suma de experiencia actual a partir de la llave provista decodificada.
     * @param token Permiso de acceso con confirmación para la verificación del estudiante actual individual.
     * @param cursoId Identificativo del elemento formativo base.
     * @return Conteo int con los puntos recogidos ya de módulos ganadores en éxito por solución.
     */
    public int getPuntosCompletadosEnCurso(String token, Long cursoId) {
        Usuario usuario = usuarioDAO.findByToken(token).orElseThrow();
        Curso curso = cursoDAO.findById(cursoId).orElseThrow();
        return getPuntosCompletadosEnCurso(usuario, curso);
    }

    /**
     * Operatiza la obtención del número que expone cuál es la cantidad o tope máximo conseguible si uno finaliza todo favorablemente en el curso.
     * @param cursoId Constante en el sistema para encontrar a todos sus ejercicios enlazados.
     * @return El cúmulo o cota final Int de la materia total del susodicho grado.
     */
    public int getTotalPuntosByCurso(Long cursoId) {
        cursoDAO.findById(cursoId).orElseThrow();
        Integer total = cursoDAO.getTotalPuntosByCursoId(cursoId);
        return total != null ? total : 0;
    }

    /**
     * Construye un conglomerado con toda la matriz de progresos internos de cada uno de sus alumnos inscriptos con porcentajes exactos relativos evaluativos.
     * @param token Identidad certificadora en cadena verificando autoría docente o rango elevado de la petición en la DB.
     * @param cursoId Identificativo en ID del lugar donde radican todas las estadísticas a recopilar (el curso general).
     * @return Transacción DTO en la que están almacenadas todas las iteraciones o interacciones y promedios formativos internos. Nulo frente a autorizaciones truncas.
     */
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
    
    @Transactional
    /**
     * Recrea el entorno o valores nominales para las descripciones, etiquetas o metadatos de un nivel instruccional sin romper sus estructuras internas, salvo que consten borrados.
     * @param token Control de jerarquía demostrando tener derechos como autor.
     * @param cursoId Enclave genérico de persistencia identificando el componente materia a reformar en la red.
     * @param cursoUpdateDTO Carga útil del bloque general conteniendo las variaciones posibles de todos sus registros menores actualizables.
     * @return Genera o regenera y devuelve para renderización local la persistencia o modelo del Curso completado y persistido.
     */
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