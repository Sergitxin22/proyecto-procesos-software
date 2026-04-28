package com.sergitxin.flexilearn;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.external.AuthExternalFactory;
import com.sergitxin.flexilearn.external.AuthExternalPort;
import com.sergitxin.flexilearn.external.AuthProvider;
import com.sergitxin.flexilearn.external.GithubAuthExternalAdapter;
import com.sergitxin.flexilearn.external.GoogleAuthExternalAdapter;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.service.AdminService;
import com.sergitxin.flexilearn.service.AuthService;
import com.sergitxin.flexilearn.service.CursoService;
import com.sergitxin.flexilearn.service.UserService;

@ExtendWith(MockitoExtension.class)
class MockitoSanityTest {

	@Mock
	List<String> list;

	@Test
	void testDependenciaMockito() {
		when(list.get(0)).thenReturn("test");

		assertEquals("test", list.get(0));
		verify(list).get(0);
	}
}

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

	@Mock
	private UsuarioDao usuarioDAO;

	@Mock
	private CursoDAO cursoDAO;

	@Mock
	private ModuloDAO moduloDAO;

	@Mock
	private EjercicioDAO ejercicioDAO;

	@InjectMocks
	private CursoService cursoService;

	@Test
	void crearCursoTest() {
		// Creamos los objetos del modelo
		String token = "abc";
		Usuario user = new Usuario();
		user.setId(1L);

		Curso curso = new Curso();
		curso.setId(10L);

		// Con esto simulamos la interacción con la BD para abstraerla
		when(usuarioDAO.findByToken(token)).thenReturn(Optional.of(user));
		when(cursoDAO.save(any(Curso.class))).thenReturn(curso);

		// Llamamos al metodo que queremos probar
		Long result = cursoService.crearCurso(token, "Java", "Backend", "desc", Dificultad.MEDIO);

		// Hacemos el test
		assertEquals(10L, result);

		// Comprobamos si realmente se han llamado a los métodos de la BD
		verify(usuarioDAO).findByToken(token);
		verify(cursoDAO).save(any(Curso.class));
	}

	@Test
	void crearModuloTest() {
		Long cursoId = 10L;

		Curso curso = new Curso();
		curso.setId(10L);
		curso.setModulos(new ArrayList<>());

		Modulo modulo = new Modulo();
		modulo.setId(5L);

		when(cursoDAO.findById(cursoId)).thenReturn(Optional.of(curso));
		when(moduloDAO.save(any(Modulo.class))).thenReturn(modulo);

		Long result = cursoService.crearModulo("Modulo", "Descripción", cursoId);

		assertEquals(5L, result);

		verify(cursoDAO).findById(cursoId);
		verify(moduloDAO).save(any(Modulo.class));
	}

	@Test
	void crearEjercicioTest() {
		Curso curso = new Curso();
		curso.setId(10L);
		curso.setModulos(new ArrayList<>());

		Modulo modulo = new Modulo();
		modulo.setId(5L);
		modulo.setEjercicios(new ArrayList<>());

		Ejercicio ejercicio = new Ejercicio();
		ejercicio.setId(3L);

		when(moduloDAO.findById(modulo.getId())).thenReturn(Optional.of(modulo));
		when(ejercicioDAO.save(any(Ejercicio.class))).thenReturn(ejercicio);

		Long result = cursoService.crearEjercicio("Ejercicio", "Teoría", "Código", 3, "Enunciado", "Lenguaje",
				modulo.getId());

		assertEquals(3L, result);

		verify(ejercicioDAO).save(any(Ejercicio.class));
	}

	@Test
	void getCursoTest() {
		Curso curso = new Curso();
		curso.setId(10L);

		when(cursoDAO.findById(10L)).thenReturn(Optional.of(curso));

		Curso result = cursoService.getCurso(10L);

		assertEquals(10L, result.getId());
		verify(cursoDAO).findById(10L);

		when(cursoDAO.findById(10L)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> {
			cursoService.getCurso(10L);
		});
	}

	@Test
	void getCursosTest() {
		List<Curso> cursos = List.of(new Curso(), new Curso());

		when(cursoDAO.findAll()).thenReturn(cursos);

		List<Curso> result = cursoService.getAllCursos();

		assertEquals(2, result.size());
		verify(cursoDAO).findAll();
	}

	@Test
	void getCursosMatriculadosTest() {
		String token = "abc";

		List<Curso> cursos = new ArrayList<>(List.of(new Curso(), new Curso()));

		Usuario user = new Usuario();
		user.setCursosMatriculados(cursos);

		when(usuarioDAO.findByToken(token)).thenReturn(Optional.of(user));

		List<Curso> result = cursoService.getCursosMatriculados(token);

		assertEquals(2, result.size());
		verify(usuarioDAO).findByToken(token);
	}

	@Test
	void matricularUsuarioAddsCursoTest() {
		String token = "abc";
		Long cursoId = 10L;

		Curso curso = new Curso();
		curso.setId(cursoId);

		Usuario user = new Usuario();
		user.setCursosMatriculados(new ArrayList<>());

		when(usuarioDAO.findByToken(token)).thenReturn(Optional.of(user));
		when(cursoDAO.findById(cursoId)).thenReturn(Optional.of(curso));

		cursoService.matricularUsuario(token, cursoId);

		assertTrue(user.getCursosMatriculados().contains(curso));

		verify(usuarioDAO).save(user);
		verify(usuarioDAO).findByToken(token);
		verify(cursoDAO).findById(cursoId);
	}

	@Test
	void matricularUsuarioAlreadyEnrolledDoesNotSaveTest() {
		String token = "abc";
		Long cursoId = 10L;

		Curso curso = new Curso();
		curso.setId(cursoId);

		List<Curso> cursos = new ArrayList<>();
		cursos.add(curso);

		Usuario user = new Usuario();
		user.setCursosMatriculados(cursos);

		when(usuarioDAO.findByToken(token)).thenReturn(Optional.of(user));
		when(cursoDAO.findById(cursoId)).thenReturn(Optional.of(curso));

		cursoService.matricularUsuario(token, cursoId);

		assertEquals(1, user.getCursosMatriculados().size());

		verify(usuarioDAO, never()).save(any());
	}
}

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

	@Mock
	private UsuarioDao usuarioDao;

	@InjectMocks
	private UserService userService;

	@Test
	void obtenerCursosDeUsuarioTest() {
		String token = "abc";

		List<Curso> cursos = new ArrayList<>();
		cursos.add(new Curso());
		cursos.add(new Curso());

		Usuario usuario = new Usuario();
		usuario.setCursosCreados(cursos);

		when(usuarioDao.findByToken(token)).thenReturn(Optional.of(usuario));

		List<Curso> result = userService.obtenerCursosdeUsuario(token);
		assertEquals(2, result.size());
		assertEquals(cursos, result);

		verify(usuarioDao).findByToken(token);
	}
}

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UsuarioDao usuarioDao;

	@Mock
	private AuthExternalFactory authExternalFactory;

	@Mock
	private AuthExternalPort authExternalPort;

	@InjectMocks
	private AuthService authService;

	@Test
	void generateSaltTest() {
		byte[] salt1 = AuthService.generateSalt();
		byte[] salt2 = AuthService.generateSalt();

		assertEquals(16, salt1.length);
		assertEquals(16, salt2.length);
		assertFalse(Arrays.equals(salt1, salt2));
	}

	@Test
	void hashPasswordTest() throws Exception {
		String password = "Contraseña";
		byte[] salt = AuthService.generateSalt();

		String hash1 = AuthService.hashPassword(password, salt);
		String hash2 = AuthService.hashPassword(password, salt);

		assertNotNull(hash1);
		assertEquals(hash1, hash2);
	}

	@Test
	void obtenerUsuarioByTokenNotFoundTest() {
		when(usuarioDao.findByToken("abc")).thenReturn(Optional.empty());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> {
			authService.obtenerUsuarioByToken("abc");
		});

		assertEquals("Usuario no encontrado para el token proporcionado", ex.getMessage());
	}

	@Test
	void registrarUsuarioEmailExistsTest() {
		when(usuarioDao.existsByEmail("Email")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> {
			authService.registrarUsuario("Usuario", "Email", "Contraseña");
		});

		verify(usuarioDao, never()).save(any());
	}

	@Test
	void registrarUsuarioTest() {
		when(usuarioDao.existsByEmail("Email")).thenReturn(false);
		authService.registrarUsuario("Usuario", "Email", "Contraseña");
		verify(usuarioDao).save(any(Usuario.class));
	}

	@Test
	void obtenerTodosLosUsuariosTest() {
		List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
		when(usuarioDao.findAll()).thenReturn(usuarios);
		List<Usuario> result = authService.obtenerTodosLosUsuarios();

		assertEquals(2, result.size());
		verify(usuarioDao).findAll();
	}

	@Test
	void obtenerUsuarioByTokenTest() {
		Usuario user = new Usuario();

		when(usuarioDao.findByToken("abc")).thenReturn(Optional.of(user));

		Usuario result = authService.obtenerUsuarioByToken("abc");

		assertEquals(user, result);
	}

	@Test
	void iniciarSesionContraseñaIncorrectaTest() {
		String email = "Email";

		Usuario user = new Usuario();
		user.setPassword("hashmalo");

		when(usuarioDao.findByEmail(email)).thenReturn(Optional.of(user));

		when(authExternalFactory.createAuthAdapter(AuthProvider.GOOGLE)).thenReturn(authExternalPort);
		when(authExternalPort.validarTokenExterno(anyString())).thenReturn(true);

		assertThrows(RuntimeException.class, () -> {
			authService.iniciarSesion(email, "wrong");
		});
	}

	@Test
	void iniciarSesionTest() throws Exception {
		String email = "Email";
		String password = "Contraseña";

		byte[] salt = AuthService.generateSalt();
		String hash = AuthService.hashPassword(password, salt);
		String storedPassword = Base64.getEncoder().encodeToString(salt) + ":" + hash;

		Usuario user = new Usuario();
		user.setPassword(storedPassword);

		when(usuarioDao.findByEmail(email)).thenReturn(Optional.of(user));

		when(authExternalFactory.createAuthAdapter(AuthProvider.GOOGLE)).thenReturn(authExternalPort);
		when(authExternalPort.validarTokenExterno(anyString())).thenReturn(true);

		String token = authService.iniciarSesion(email, password);

		assertNotNull(token);
		assertNotNull(user.getToken());

		verify(usuarioDao).save(user);
	}

	@Test
	void cerrarSesionNotFoundTest() {
		when(usuarioDao.findByToken("abc")).thenReturn(Optional.empty());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> {
			authService.cerrarSesion("abc");
		});

		assertEquals("Sesión no encontrada o ya finalizada", ex.getMessage());

		verify(usuarioDao, never()).save(any());
	}

	@Test
	void cerrarSesionTest() {
		Usuario user = new Usuario();
		user.setToken("abc");

		when(usuarioDao.findByToken("abc")).thenReturn(Optional.of(user));

		authService.cerrarSesion("abc");

		assertNull(user.getToken());
		verify(usuarioDao).save(user);
	}
}

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private UsuarioDao usuarioDao;

	@Mock
	private CursoDAO cursoDao;

	@InjectMocks
	private AdminService adminService;

	@Test
	void eliminarUsuarioTest() {
		String token = "admin";
		String nombre = "Usuario";

		Usuario admin = new Usuario();
		admin.setEsAdmin(true);

		Usuario user = new Usuario();
		List<Curso> cursos = new ArrayList<>();
		user.setCursosCreados(cursos);

		when(usuarioDao.findByToken(token)).thenReturn(Optional.of(admin));
		when(usuarioDao.findByNombre(nombre)).thenReturn(Optional.of(user));

		boolean result = adminService.eliminarUsuario(token, nombre);

		assertTrue(result);

		verify(cursoDao).deleteAll(cursos);
		verify(usuarioDao).delete(user);

		token = "user";

		Usuario normalUser = new Usuario();
		normalUser.setEsAdmin(false);

		when(usuarioDao.findByToken(token)).thenReturn(Optional.of(normalUser));

		result = adminService.eliminarUsuario(token, "juan");

		assertFalse(result);
	}

	@Test
	void eliminarUsuarioAdminNotFoundTest() {
		when(usuarioDao.findByToken("token")).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> {
			adminService.eliminarUsuario("token", "juan");
		});
	}

	@Test
	void getAllUsersTest() {
		List<Usuario> users = List.of(new Usuario(), new Usuario());

		when(usuarioDao.findAll()).thenReturn(users);

		List<Usuario> result = adminService.getAllUsers();

		assertEquals(2, result.size());
		assertEquals(users, result);

		verify(usuarioDao).findAll();
	}
}

@ExtendWith(MockitoExtension.class)
class AuthExternalFactoryTest {
	private final AuthExternalFactory factory = new AuthExternalFactory();

	@Test
	void createAuthAdapterGoogleTest() {
		AuthExternalPort result = factory.createAuthAdapter(AuthProvider.GOOGLE);

		assertNotNull(result);
		assertTrue(result instanceof GoogleAuthExternalAdapter);
	}

	@Test
	void createAuthAdapterGithubTest() {
		AuthExternalPort result = factory.createAuthAdapter(AuthProvider.GITHUB);

		assertNotNull(result);
		assertTrue(result instanceof GithubAuthExternalAdapter);
	}

	@Test
	void createAuthAdapterNullProviderTest() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> factory.createAuthAdapter(null));

		assertEquals("Proveedor de autenticación no válido", ex.getMessage());
	}
}

@ExtendWith(MockitoExtension.class)
class GithubAuthExternalAdapterTest {

	private final GithubAuthExternalAdapter adapter = new GithubAuthExternalAdapter();

	@Test
	void validarTokenExternoValidGhpTest() {
		String token = "ghp_123456";

		boolean result = adapter.validarTokenExterno(token);

		assertTrue(result);
	}

	@Test
	void validarTokenExternoValidGhoTest() {
		String token = "gho_abcdef";

		boolean result = adapter.validarTokenExterno(token);

		assertTrue(result);
	}

	@Test
	void validarTokenExternoInvalidTokenTest() {
		String token = "invalid_token";

		boolean result = adapter.validarTokenExterno(token);

		assertFalse(result);
	}

	@Test
	void validarTokenExternoNullTokenTest() {
		boolean result = adapter.validarTokenExterno(null);

		assertFalse(result);
	}
}

@ExtendWith(MockitoExtension.class)
class GoogleAuthExternalAdapterTest {

	private final GoogleAuthExternalAdapter adapter = new GoogleAuthExternalAdapter();

	@Test
	void validarTokenExternoValidGoogleTokenTest() {
		String token = "google-token-12345";

		boolean result = adapter.validarTokenExterno(token);

		assertTrue(result);
	}

	@Test
	void validarTokenExternoInvalidTokenTest() {
		String token = "ghp_invalid";

		boolean result = adapter.validarTokenExterno(token);

		assertFalse(result);
	}

	@Test
	void validarTokenExternoNullTokenTest() {
		boolean result = adapter.validarTokenExterno(null);

		assertFalse(result);
	}
}

class CursoTest {

	@Test
	void cursoGettersAndSettersTest() {
		Curso curso = new Curso();

		curso.setId(1L);
		curso.setNombre("Java");
		curso.setCategoria("Backend");
		curso.setDescripcion("Desc");
		curso.setDificultad(Dificultad.MEDIO);

		assertEquals(1L, curso.getId());
		assertEquals("Java", curso.getNombre());
		assertEquals("Backend", curso.getCategoria());
		assertEquals("Desc", curso.getDescripcion());
		assertEquals(Dificultad.MEDIO, curso.getDificultad());
	}

	@Test
	void cursoCollectionsTest() {
		Curso curso = new Curso();

		Usuario usuario = new Usuario();
		usuario.setId(1L);

		curso.setUsuariosMatriculados(new ArrayList<>());
		curso.getUsuariosMatriculados().add(usuario);

		assertEquals(1, curso.getUsuariosMatriculados().size());
		assertEquals(usuario, curso.getUsuariosMatriculados().get(0));
	}
}