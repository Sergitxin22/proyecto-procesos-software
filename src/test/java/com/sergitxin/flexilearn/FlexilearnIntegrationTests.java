package com.sergitxin.flexilearn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;
import java.util.List;

@SpringBootTest
@Transactional
class FlexilearnIntegrationTests {
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UsuarioDao usuarioDao;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	private String registerAndLogin(String nombre, String email, String password) throws Exception {
		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", nombre, "email", email, "password", password))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
				.andExpect(status().isOk()).andReturn();

		return objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();
	}

	private Long createCurso(String token) throws Exception {
		var res = mockMvc.perform(post("/api/courses/").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Curso Test", "categoria", "Programacion",
						"descripcion", "Descripción", "dificultad", "facil"))))
				.andExpect(status().isOk()).andReturn();
		return Long.parseLong(res.getResponse().getContentAsString());
	}

	private Long createModulo(Long cursoId) throws Exception {
		var res = mockMvc.perform(post("/api/courses/modules").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
						Map.of("nombre", "Módulo 1", "descripcion", "Descripción del módulo", "idCurso", cursoId))))
				.andExpect(status().isOk()).andReturn();
		return Long.parseLong(res.getResponse().getContentAsString());
	}

	private Long createEjercicio(Long moduloId) throws Exception {
		var res = mockMvc.perform(post("/api/courses/exercises").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Ejercicio 1", "teoria", "Teoría",
						"codigoInicial", "// code", "puntos", 10, "enunciado", "Enunciado", "lenguaje", "java",
						"idModulo", moduloId))))
				.andExpect(status().isOk()).andReturn();
		return Long.parseLong(res.getResponse().getContentAsString());
	}

	private String getAdminToken(String email) throws Exception {
		String token = registerAndLogin("Admin", email, "adminpass");
		Usuario usuario = usuarioDao.findByToken(token).get();
		usuario.setEsAdmin(true);
		usuarioDao.save(usuario);
		return token;
	}

	private void enrollCourse(Long cursoId, String token) throws Exception {
		mockMvc.perform(post("/api/courses/" + cursoId + "/enroll")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andExpect(jsonPath("$.mensaje").value("Matriculado correctamente"));
	}

	@Test
	void testAuthRegistrar() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Paco", "email", email, "password", "123"))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.mensaje").value("Usuario registrado exitosamente"));

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Aitor", "email", email, "password", "hola"))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testAuthLogin() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Paco", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.mensaje").value("Inicio de sesión exitoso"));

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "mal"))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void testAuthLogout() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";
		String token = registerAndLogin("Paco", email, "123");

		mockMvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Sesión finalizada correctamente"));

		mockMvc.perform(post("/api/auth/logout")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetUsuarioByToken() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";
		String token = registerAndLogin("Paco", email, "123");

		mockMvc.perform(get("/api/auth/user").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(email)).andExpect(jsonPath("$.nombre").value("Paco"));

		mockMvc.perform(get("/api/auth/user")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetAllUsers() throws Exception {
		mockMvc.perform(get("/api/auth/users")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	@Test
	void testAdminDeleteUser() throws Exception {
		String emailUser = "paco" + UUID.randomUUID() + "@paco.com";
		String emailAdmin = "aroa" + UUID.randomUUID() + "@aroa.com";

		registerAndLogin("Paco", emailUser, "123");
		String adminToken = registerAndLogin("Aroa", emailAdmin, "456");

		mockMvc.perform(delete("/api/admin/deleteUser").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombreUsuario", "User"))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));

		mockMvc.perform(delete("/api/admin/deleteUser").header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombreUsuario", "Paco")))).andExpect(status().isOk());
	}

	@Test
	void testAdminGetAllUsers() throws Exception {
		mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	@Test
	void testCreateCurso() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";
		String token = registerAndLogin("Markel", email, "123");

		mockMvc.perform(post("/api/courses/").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Curso de Java", "categoria", "Programacion",
						"descripcion", "Curso de Java para principiantes", "dificultad", "facil"))))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/courses/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Python Avanzado", "categoria",
						"Programacion", "descripcion", "Curso de Python avanzado", "dificultad", "dificil"))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testCreateModulo() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";
		String token = registerAndLogin("Markel", email, "123");
		Long cursoId = createCurso(token);

		mockMvc.perform(post("/api/courses/modules").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
						Map.of("nombre", "Módulo 1", "descripcion", "Descripción del módulo", "idCurso", cursoId))))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateEjercicio() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";
		String token = registerAndLogin("Markel", email, "123");
		Long cursoId = createCurso(token);
		Long moduloId = createModulo(cursoId);

		mockMvc.perform(post("/api/courses/exercises").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Ejercicio 1", "teoria",
						"Teoría del ejercicio", "codigoInicial", "public class Main {}", "puntos", 10, "enunciado",
						"Enunciado del ejercicio", "lenguaje", "java", "idModulo", moduloId))))
				.andExpect(status().isOk());
	}

	@Test
	void testGetCourses() throws Exception {
		mockMvc.perform(get("/api/courses/")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	@Test
	void testGetCourseById() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";
		String token = registerAndLogin("Markel", email, "123");
		Long cursoId = createCurso(token);

		mockMvc.perform(get("/api/courses/" + cursoId + "/")).andExpect(status().isOk());
	}

	@Test
	void testEnrollCourse() throws Exception {
		String emailCreator = "aroa" + UUID.randomUUID() + "@aroa.com";
		String emailStudent = "markel-" + UUID.randomUUID() + "@markel.com";

		String creatorToken = registerAndLogin("Aroa", emailCreator, "123");
		Long cursoId = createCurso(creatorToken);

		String studentToken = registerAndLogin("Markel", emailStudent, "123");

		enrollCourse(cursoId, studentToken);

		mockMvc.perform(post("/api/courses/" + cursoId + "/enroll")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetEnrolledCourses() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";
		String token = registerAndLogin("Paco", email, "123");

		mockMvc.perform(get("/api/courses/enrolled").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

		mockMvc.perform(get("/api/courses/enrolled")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetUserCreatedCourses() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";
		String token = registerAndLogin("Markel", email, "123");

		mockMvc.perform(get("/api/users/createdCourses").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/users/createdCourses")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testAuthDeleteAccount() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";
		String token = registerAndLogin("Markel", email, "123");

		mockMvc.perform(delete("/api/auth/delete"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));

		mockMvc.perform(delete("/api/auth/delete").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateExerciseTests() throws Exception {
		String email = "tests-" + UUID.randomUUID() + "@test.com";
		String token = registerAndLogin("TestUser", email, "123");

		Long cursoId = createCurso(token);
		Long moduloId = createModulo(cursoId);
		Long ejercicioId = createEjercicio(moduloId);

		mockMvc.perform(post("/api/courses/exercices/" + ejercicioId + "/tests")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
						List.of(Map.of("codigo", "input1", "salidaEsperada", "output1")))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));

		mockMvc.perform(post("/api/courses/exercices/" + ejercicioId + "/tests")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
						List.of(Map.of("codigo", "input1", "salidaEsperada", "output1")))))
				.andExpect(status().isOk());
	}

	@Test
	void testGetExerciseById() throws Exception {
		String email = "exget-" + UUID.randomUUID() + "@test.com";
		String token = registerAndLogin("ExUser", email, "123");

		Long cursoId = createCurso(token);
		Long moduloId = createModulo(cursoId);
		Long ejercicioId = createEjercicio(moduloId);

		mockMvc.perform(get("/api/courses/exercises/" + ejercicioId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(ejercicioId))
				.andExpect(jsonPath("$.nombre").value("Ejercicio 1"));
	}

	@Test
	void testDeleteCursoByProfesor() throws Exception {
		String email = "aroa" + UUID.randomUUID() + "@aroa.com";
		String token = registerAndLogin("Aroa", email, "123");

		Long cursoId = createCurso(token);

		mockMvc.perform(delete("/api/courses/deleteCurso").param("cursoId", cursoId.toString()))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));

		mockMvc.perform(delete("/api/courses/deleteCurso")
				.header("Authorization", "Bearer " + token)
				.param("cursoId", cursoId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));
	}

	@Test
	void testSendAndGetMessages() throws Exception {
		String emailProf = "aroa" + UUID.randomUUID() + "@aroa.com";
		String emailAlumno = "markel-" + UUID.randomUUID() + "@markel.com";

		String profToken = registerAndLogin("Aroa", emailProf, "123");
		String alumnoToken = registerAndLogin("Markel", emailAlumno, "123");

		Long cursoId = createCurso(profToken);

		mockMvc.perform(post("/api/courses/" + cursoId + "/enroll")
				.header("Authorization", "Bearer " + alumnoToken))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/courses/" + cursoId + "/messages")
				.header("Authorization", "Bearer " + alumnoToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("mensaje", "Hola, tengo una duda"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Hola, tengo una duda"))
				.andExpect(jsonPath("$.username").value("Markel"));

		mockMvc.perform(get("/api/courses/" + cursoId + "/messages")
				.header("Authorization", "Bearer " + alumnoToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].mensaje").value("Hola, tengo una duda"));
	}

	@Test
	void testGetCourseStats() throws Exception {
		String emailProf = "aroa" + UUID.randomUUID() + "@aroa.com";
		String emailOtro = "markel-" + UUID.randomUUID() + "@markel.com";

		String profToken = registerAndLogin("Aroa", emailProf, "123");
		String otroToken = registerAndLogin("Markel", emailOtro, "123");

		Long cursoId = createCurso(profToken);

		mockMvc.perform(get("/api/courses/" + cursoId + "/stats")
				.header("Authorization", "Bearer " + profToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalAlumnos").isNumber())
				.andExpect(jsonPath("$.totalEjercicios").isNumber())
				.andExpect(jsonPath("$.progresoAlumnos").isArray());

		mockMvc.perform(get("/api/courses/" + cursoId + "/stats")
				.header("Authorization", "Bearer " + otroToken))
				.andExpect(status().isForbidden());
	}

	@Test
	void testAdminDeleteCurso() throws Exception {
		String emailProf = "markel-" + UUID.randomUUID() + "@markel.com";
		String emailAdmin = "aroa" + UUID.randomUUID() + "@aroa.com";

		String profToken = registerAndLogin("Markel", emailProf, "123");
		String adminToken = getAdminToken(emailAdmin);

		Long cursoId = createCurso(profToken);

		mockMvc.perform(delete("/api/admin/deleteCurso").param("cursoId", cursoId.toString()))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));

		mockMvc.perform(delete("/api/admin/deleteCurso")
				.header("Authorization", "Bearer " + adminToken)
				.param("cursoId", cursoId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));
	}

	@Test
	void testAdminUsersStats() throws Exception {
		String emailAdmin = "aroa" + UUID.randomUUID() + "@aroa.com";
		String emailUser = "markel-" + UUID.randomUUID() + "@markel.com";

		String adminToken = getAdminToken(emailAdmin);
		String userToken = registerAndLogin("Markel", emailUser, "123");

		mockMvc.perform(get("/api/admin/usersStats"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));

		mockMvc.perform(get("/api/admin/usersStats")
				.header("Authorization", "Bearer " + userToken))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/admin/usersStats")
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].nombre").exists())
				.andExpect(jsonPath("$[0].cursosCreados").isNumber())
				.andExpect(jsonPath("$[0].ejerciciosCompletados").isNumber());
	}
}