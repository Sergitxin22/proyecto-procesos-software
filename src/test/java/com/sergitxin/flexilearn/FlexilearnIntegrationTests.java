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

@SpringBootTest
@Transactional
class FlexilearnIntegrationTests {
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testAuthRegistrar() throws Exception {
		String email = "paco-" + UUID.randomUUID() + "@paco.com";

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
		String email = "paco-" + UUID.randomUUID() + "@paco.com";

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

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Paco", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token").exists()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

		mockMvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Sesión finalizada correctamente"));

		mockMvc.perform(post("/api/auth/logout")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetUsuarioByToken() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Paco", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

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

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Paco", "email", emailUser, "password", "123"))))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Aroa", "email", emailAdmin, "password", "456"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", emailAdmin, "password", "456"))))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token").exists()).andReturn();

		String adminToken = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token")
				.asText();

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

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Markel", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

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

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Markel", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

		var cursoResponse = mockMvc
				.perform(post("/api/courses/").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("nombre", "Curso Test", "categoria",
								"Programacion", "descripcion", "Curso de prueba", "dificultad", "facil"))))
				.andExpect(status().isOk()).andReturn();

		String cursoResponseBody = cursoResponse.getResponse().getContentAsString();
		Long cursoId = Long.parseLong(cursoResponseBody);

		mockMvc.perform(post("/api/courses/modules").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
						Map.of("nombre", "Módulo 1", "descripcion", "Descripción del módulo", "idCurso", cursoId))))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateEjercicio() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Markel", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

		var cursoResponse = mockMvc
				.perform(post("/api/courses/").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("nombre", "Curso Test", "categoria",
								"Programacion", "descripcion", "Curso de prueba", "dificultad", "facil"))))
				.andExpect(status().isOk()).andReturn();

		String cursoResponseBody = cursoResponse.getResponse().getContentAsString();
		Long cursoId = Long.parseLong(cursoResponseBody);

		var moduloResponse = mockMvc
				.perform(
						post("/api/courses/modules").contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(Map.of("nombre", "Módulo 1", "descripcion",
										"Descripción del módulo", "idCurso", cursoId))))
				.andExpect(status().isOk()).andReturn();

		String moduloResponseBody = moduloResponse.getResponse().getContentAsString();
		Long moduloId = Long.parseLong(moduloResponseBody);

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

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Markel", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

		var cursoResponse = mockMvc
				.perform(post("/api/courses/").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("nombre", "Curso Test", "categoria",
								"Programacion", "descripcion", "Curso de prueba", "dificultad", "facil"))))
				.andExpect(status().isOk()).andReturn();

		Long cursoId = Long.parseLong(cursoResponse.getResponse().getContentAsString());

		mockMvc.perform(get("/api/courses/" + cursoId + "/")).andExpect(status().isOk());
	}

	@Test
	void testEnrollCourse() throws Exception {
		String emailCreator = "creator-" + UUID.randomUUID() + "@test.com";
		String emailStudent = "student-" + UUID.randomUUID() + "@test.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Creator", "email", emailCreator, "password", "123"))))
				.andExpect(status().isCreated());

		var creatorLoginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", emailCreator, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String creatorToken = objectMapper.readTree(creatorLoginResponse.getResponse().getContentAsString())
				.get("token").asText();

		var cursoResponse = mockMvc
				.perform(post("/api/courses/").header("Authorization", "Bearer " + creatorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("nombre", "Curso Test", "categoria",
								"Programacion", "descripcion", "Curso de prueba", "dificultad", "facil"))))
				.andExpect(status().isOk()).andReturn();

		Long cursoId = Long.parseLong(cursoResponse.getResponse().getContentAsString());

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Student", "email", emailStudent, "password", "123"))))
				.andExpect(status().isCreated());

		var studentLoginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", emailStudent, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String studentToken = objectMapper.readTree(studentLoginResponse.getResponse().getContentAsString())
				.get("token").asText();

		mockMvc.perform(post("/api/courses/" + cursoId + "/enroll").header("Authorization", "Bearer " + studentToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.mensaje").value("Matriculado correctamente"));

		mockMvc.perform(post("/api/courses/" + cursoId + "/enroll")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetEnrolledCourses() throws Exception {
		String email = "paco" + UUID.randomUUID() + "@paco.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of("nombre", "Paco", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

		mockMvc.perform(get("/api/courses/enrolled").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

		mockMvc.perform(get("/api/courses/enrolled")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

	@Test
	void testGetUserCreatedCourses() throws Exception {
		String email = "markel-" + UUID.randomUUID() + "@markel.com";

		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(Map.of("nombre", "Markel", "email", email, "password", "123"))))
				.andExpect(status().isCreated());

		var loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("email", email, "password", "123"))))
				.andExpect(status().isOk()).andReturn();

		String token = objectMapper.readTree(loginResponse.getResponse().getContentAsString()).get("token").asText();

		mockMvc.perform(get("/api/users/createdCourses").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/users/createdCourses")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Token no proporcionado o inválido"));
	}

}