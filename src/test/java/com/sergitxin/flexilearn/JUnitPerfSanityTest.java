package com.sergitxin.flexilearn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.dao.TestDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.external.AuthExternalFactory;
import com.sergitxin.flexilearn.service.AdminService;
import com.sergitxin.flexilearn.service.AuthService;
import com.sergitxin.flexilearn.service.CursoService;
import com.sergitxin.flexilearn.service.UserService;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JUnitPerfInterceptor.class)
class JUnitPerfSanityTest {

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 200)
    void authService_hashPassword_generaHashDeterministaConMismaSalt() throws InvalidKeySpecException {
        byte[] salt = new byte[16];
        String hash1 = AuthService.hashPassword("1234", salt);
        String hash2 = AuthService.hashPassword("1234", salt);
        String hash3 = AuthService.hashPassword("4321", salt);

        assertEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 200)
    void authService_generateSalt_generaSaltConLongitudEsperadaYAleatoria() {
        byte[] salt1 = AuthService.generateSalt();
        byte[] salt2 = AuthService.generateSalt();

        assertEquals(16, salt1.length);
        assertEquals(16, salt2.length);
        assertFalse(java.util.Arrays.equals(salt1, salt2));
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_500, warmUpMs = 200)
    void authService_hashPassword_repiteOperacionConResultadoValido() throws InvalidKeySpecException {
        byte[] salt = AuthService.generateSalt();
        String hash = AuthService.hashPassword("password-performance", salt);

        assertNotEquals("", hash);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void adminService_eliminarUsuario_adminTrue() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDao = cursoDao(db);
        AdminService service = new AdminService(usuarioDao, cursoDao);

        Usuario admin = new Usuario();
        admin.setNombre("admin");
        admin.setEmail("admin@t.com");
        admin.setToken("token-admin");
        admin.setEsAdmin(true);
        usuarioDao.save(admin);

        Curso curso = new Curso();
        curso.setNombre("Curso A");
        cursoDao.save(curso);

        Usuario user = new Usuario();
        user.setNombre("pepe");
        user.setEmail("pepe@t.com");
        user.setCursosCreados(new ArrayList<>(List.of(curso)));
        usuarioDao.save(user);

        boolean ok = service.eliminarUsuario("token-admin", "pepe");
        assertTrue(ok);
        assertTrue(usuarioDao.findByNombre("pepe").isEmpty());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void adminService_eliminarUsuario_adminFalse() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDao = cursoDao(db);
        AdminService service = new AdminService(usuarioDao, cursoDao);

        Usuario notAdmin = new Usuario();
        notAdmin.setNombre("user");
        notAdmin.setEmail("user@t.com");
        notAdmin.setToken("token-user");
        notAdmin.setEsAdmin(false);
        usuarioDao.save(notAdmin);

        boolean ok = service.eliminarUsuario("token-user", "pepe");
        assertFalse(ok);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void adminService_getAllUsers() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDao = cursoDao(db);
        AdminService service = new AdminService(usuarioDao, cursoDao);

        Usuario u1 = new Usuario();
        u1.setNombre("u1");
        u1.setEmail("u1@t.com");
        usuarioDao.save(u1);

        Usuario u2 = new Usuario();
        u2.setNombre("u2");
        u2.setEmail("u2@t.com");
        usuarioDao.save(u2);

        assertEquals(2, service.getAllUsers().size());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 100)
    void authService_registrarUsuario_ok() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        String email = "mail-" + System.nanoTime() + "@test.com";
        service.registrarUsuario("Pepe", email, "1234");

        Usuario saved = usuarioDao.findByEmail(email).orElseThrow();
        assertEquals("Pepe", saved.getNombre());
        assertTrue(saved.getPassword().contains(":"));
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 100)
    void authService_registrarUsuario_emailDuplicado() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        Usuario user = new Usuario();
        user.setNombre("Pepe");
        user.setEmail("dup@test.com");
        user.setPassword("x:y");
        usuarioDao.save(user);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.registrarUsuario("Pepe", "dup@test.com", "1234"));
        assertEquals("El correo ya está registrado", ex.getMessage());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void authService_obtenerTodosLosUsuarios() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        Usuario u1 = new Usuario();
        u1.setNombre("u1");
        u1.setEmail("u1@test.com");
        usuarioDao.save(u1);
        Usuario u2 = new Usuario();
        u2.setNombre("u2");
        u2.setEmail("u2@test.com");
        usuarioDao.save(u2);

        assertEquals(2, service.obtenerTodosLosUsuarios().size());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void authService_obtenerUsuarioByToken_ok() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        Usuario user = new Usuario();
        user.setNombre("u");
        user.setEmail("u@test.com");
        user.setToken("tok");
        usuarioDao.save(user);

        assertEquals(user, service.obtenerUsuarioByToken("tok"));
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void authService_obtenerUsuarioByToken_fail() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerUsuarioByToken("nope"));
        assertEquals("Usuario no encontrado para el token proporcionado", ex.getMessage());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 100)
    void authService_iniciarSesion_ok() throws InvalidKeySpecException {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        byte[] salt = AuthService.generateSalt();
        String saltString = Base64.getEncoder().encodeToString(salt);
        String hash = AuthService.hashPassword("1234", salt);

        Usuario user = new Usuario();
        user.setNombre("u");
        user.setEmail("u@test.com");
        user.setPassword(saltString + ":" + hash);
        usuarioDao.save(user);

        String token = service.iniciarSesion("u@test.com", "1234");
        assertNotNull(token);
        assertNotNull(usuarioDao.findByEmail("u@test.com").orElseThrow().getToken());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 100)
    void authService_iniciarSesion_fail() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.iniciarSesion("no@test.com", "1234"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 100)
    void authService_cerrarSesion_ok() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        Usuario user = new Usuario();
        user.setNombre("u");
        user.setEmail("u@test.com");
        user.setToken("tok");
        usuarioDao.save(user);

        service.cerrarSesion("tok");
        assertNull(usuarioDao.findByEmail("u@test.com").orElseThrow().getToken());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_200, warmUpMs = 100)
    void authService_cerrarSesion_fail() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        AuthService service = new AuthService(usuarioDao, new AuthExternalFactory());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cerrarSesion("nope"));
        assertEquals("Sesión no encontrada o ya finalizada", ex.getMessage());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_crearCurso() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Usuario user = new Usuario();
        user.setNombre("prof");
        user.setEmail("prof@test.com");
        user.setToken("tok");
        usuarioDao.save(user);

        Long id = service.crearCurso("tok", "Java", "Backend", "Curso", Dificultad.FACIL);
        assertNotNull(id);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_crearModulo() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Curso curso = new Curso();
        curso.setNombre("Curso");
        curso.setModulos(new ArrayList<>());
        cursoDAO.save(curso);

        Long id = service.crearModulo("Modulo 1", "Desc", curso.getId());
        assertNotNull(id);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_crearEjercicio() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Modulo modulo = new Modulo();
        modulo.setNombre("Modulo 1");
        modulo.setEjercicios(new ArrayList<>());
        moduloDAO.save(modulo);

        Long id = service.crearEjercicio("Ej", "Teoria", "", 10, "Enunciado", "java", modulo.getId());
        assertNotNull(id);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_getCurso() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Curso curso = new Curso();
        curso.setNombre("Curso");
        cursoDAO.save(curso);

        assertEquals(curso, service.getCurso(curso.getId()));
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_getAllCursos() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Curso c1 = new Curso();
        c1.setNombre("C1");
        cursoDAO.save(c1);
        Curso c2 = new Curso();
        c2.setNombre("C2");
        cursoDAO.save(c2);

        assertEquals(2, service.getAllCursos().size());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_matricularUsuario() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Usuario user = new Usuario();
        user.setNombre("u");
        user.setEmail("u@test.com");
        user.setToken("tok");
        user.setCursosMatriculados(new ArrayList<>());
        usuarioDao.save(user);

        Curso curso = new Curso();
        curso.setNombre("Curso");
        cursoDAO.save(curso);

        service.matricularUsuario("tok", curso.getId());
        assertEquals(1, user.getCursosMatriculados().size());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void cursoService_getCursosMatriculados() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        CursoDAO cursoDAO = cursoDao(db);
        ModuloDAO moduloDAO = moduloDao(db);
        EjercicioDAO ejercicioDAO = ejercicioDao(db);
        TestDAO testDAO = testDao(db);
        CursoService service = new CursoService(usuarioDao, cursoDAO, moduloDAO, ejercicioDAO, testDAO);

        Usuario user = new Usuario();
        user.setNombre("u");
        user.setEmail("u@test.com");
        user.setToken("tok");
        Curso curso = new Curso();
        curso.setNombre("Curso");
        cursoDAO.save(curso);
        user.setCursosMatriculados(new ArrayList<>(List.of(curso)));
        usuarioDao.save(user);

        assertEquals(1, service.getCursosMatriculados("tok").size());
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void userService_obtenerCursosdeUsuario() {
        TestDb db = new TestDb();
        UsuarioDao usuarioDao = usuarioDao(db);
        UserService service = new UserService(usuarioDao);

        Curso curso = new Curso();
        curso.setNombre("Curso");
        cursoDao(db).save(curso);

        Usuario user = new Usuario();
        user.setNombre("u");
        user.setEmail("u@test.com");
        user.setToken("tok");
        user.setCursosCreados(new ArrayList<>(List.of(curso)));
        usuarioDao.save(user);

        assertEquals(1, service.obtenerCursosdeUsuario("tok").size());
    }

    private static final class TestDb {
        private final Map<Long, Usuario> usuariosById = new HashMap<>();
        private final Map<String, Usuario> usuariosByEmail = new HashMap<>();
        private final Map<String, Usuario> usuariosByToken = new HashMap<>();
        private final Map<String, Usuario> usuariosByNombre = new HashMap<>();
        private final Map<Long, Curso> cursosById = new HashMap<>();
        private final Map<Long, Modulo> modulosById = new HashMap<>();
        private final Map<Long, Ejercicio> ejerciciosById = new HashMap<>();
        private final Map<Long, com.sergitxin.flexilearn.entity.Test> testsById = new HashMap<>();
        private long usuarioSeq = 1L;
        private long cursoSeq = 1L;
        private long moduloSeq = 1L;
        private long ejercicioSeq = 1L;
        private long testSeq = 1L;
    }

    private static UsuarioDao usuarioDao(TestDb db) {
        return (UsuarioDao) Proxy.newProxyInstance(
            UsuarioDao.class.getClassLoader(),
            new Class<?>[] {UsuarioDao.class},
            (proxy, method, args) -> {
                String name = method.getName();
                if ("save".equals(name)) {
                    Usuario u = (Usuario) args[0];
                    if (u.getId() == null) {
                        u.setId(db.usuarioSeq++);
                    }
                    db.usuariosById.put(u.getId(), u);
                    if (u.getEmail() != null) {
                        db.usuariosByEmail.put(u.getEmail(), u);
                    }
                    if (u.getToken() != null) {
                        db.usuariosByToken.put(u.getToken(), u);
                    }
                    if (u.getNombre() != null) {
                        db.usuariosByNombre.put(u.getNombre(), u);
                    }
                    return u;
                }
                if ("findByEmail".equals(name)) {
                    return Optional.ofNullable(db.usuariosByEmail.get((String) args[0]));
                }
                if ("findByToken".equals(name)) {
                    return Optional.ofNullable(db.usuariosByToken.get((String) args[0]));
                }
                if ("findByNombre".equals(name)) {
                    return Optional.ofNullable(db.usuariosByNombre.get((String) args[0]));
                }
                if ("existsByEmail".equals(name)) {
                    return db.usuariosByEmail.containsKey((String) args[0]);
                }
                if ("findAll".equals(name)) {
                    return new ArrayList<>(db.usuariosById.values());
                }
                if ("delete".equals(name)) {
                    Usuario u = (Usuario) args[0];
                    if (u.getId() != null) {
                        db.usuariosById.remove(u.getId());
                    }
                    if (u.getEmail() != null) {
                        db.usuariosByEmail.remove(u.getEmail());
                    }
                    if (u.getToken() != null) {
                        db.usuariosByToken.remove(u.getToken());
                    }
                    if (u.getNombre() != null) {
                        db.usuariosByNombre.remove(u.getNombre());
                    }
                    return null;
                }
                if ("toString".equals(name)) {
                    return "UsuarioDaoProxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                throw new UnsupportedOperationException("Metodo no soportado: " + name);
            }
        );
    }

    private static CursoDAO cursoDao(TestDb db) {
        return (CursoDAO) Proxy.newProxyInstance(
            CursoDAO.class.getClassLoader(),
            new Class<?>[] {CursoDAO.class},
            (proxy, method, args) -> {
                String name = method.getName();
                if ("save".equals(name)) {
                    Curso c = (Curso) args[0];
                    if (c.getId() == null) {
                        c.setId(db.cursoSeq++);
                    }
                    db.cursosById.put(c.getId(), c);
                    return c;
                }
                if ("findById".equals(name)) {
                    return Optional.ofNullable(db.cursosById.get((Long) args[0]));
                }
                if ("findAll".equals(name)) {
                    return new ArrayList<>(db.cursosById.values());
                }
                if ("deleteAll".equals(name)) {
                    Iterable<?> iterable = (Iterable<?>) args[0];
                    for (Object item : iterable) {
                        Curso c = (Curso) item;
                        if (c.getId() != null) {
                            db.cursosById.remove(c.getId());
                        }
                    }
                    return null;
                }
                if ("toString".equals(name)) {
                    return "CursoDAOProxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                throw new UnsupportedOperationException("Metodo no soportado: " + name);
            }
        );
    }

    private static ModuloDAO moduloDao(TestDb db) {
        return (ModuloDAO) Proxy.newProxyInstance(
            ModuloDAO.class.getClassLoader(),
            new Class<?>[] {ModuloDAO.class},
            (proxy, method, args) -> {
                String name = method.getName();
                if ("save".equals(name)) {
                    Modulo m = (Modulo) args[0];
                    if (m.getId() == null) {
                        m.setId(db.moduloSeq++);
                    }
                    db.modulosById.put(m.getId(), m);
                    return m;
                }
                if ("findById".equals(name)) {
                    return Optional.ofNullable(db.modulosById.get((Long) args[0]));
                }
                if ("toString".equals(name)) {
                    return "ModuloDAOProxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                throw new UnsupportedOperationException("Metodo no soportado: " + name);
            }
        );
    }

    private static EjercicioDAO ejercicioDao(TestDb db) {
        return (EjercicioDAO) Proxy.newProxyInstance(
            EjercicioDAO.class.getClassLoader(),
            new Class<?>[] {EjercicioDAO.class},
            (proxy, method, args) -> {
                String name = method.getName();
                if ("save".equals(name)) {
                    Ejercicio e = (Ejercicio) args[0];
                    if (e.getId() == null) {
                        setEjercicioId(e, db.ejercicioSeq++);
                    }
                    db.ejerciciosById.put(e.getId(), e);
                    return e;
                }
                if ("toString".equals(name)) {
                    return "EjercicioDAOProxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                throw new UnsupportedOperationException("Metodo no soportado: " + name);
            }
        );
    }

    private static TestDAO testDao(TestDb db) {
        return (TestDAO) Proxy.newProxyInstance(
            com.sergitxin.flexilearn.entity.Test.class.getClassLoader(),
            new Class<?>[] {TestDAO.class},
            (proxy, method, args) -> {
                String name = method.getName();
                if ("save".equals(name)) {
                    com.sergitxin.flexilearn.entity.Test t = (com.sergitxin.flexilearn.entity.Test) args[0];
                    if (t.getId() == null) {
                        setTestId(t, db.testSeq++);
                    }
                    db.testsById.put(t.getId(), t);
                    return t;
                }
                if ("toString".equals(name)) {
                    return "TestDAOProxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                throw new UnsupportedOperationException("Metodo no soportado: " + name);
            }
        );
    }

    private static void setEjercicioId(Ejercicio ejercicio, Long id) {
        try {
            Field field = Ejercicio.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(ejercicio, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setTestId(com.sergitxin.flexilearn.entity.Test test, Long id) {
        try {
            Field field = com.sergitxin.flexilearn.entity.Test.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(test, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
