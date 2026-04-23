package com.sergitxin.flexilearn;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class FlexilearnIntegrationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void test() {
		fail("Not yet implemented");
	}
	
	@Test
	void testAuthRegistrar() throws Exception {
		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of(
					"nombre", "Paco",
					"email", "paco@paco.com",
					"password", "123"))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.mensaje").value("Usuario registrado exitosamente"));
		
		mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of(
					"nombre", "Aitor",
					"email", "aitor@aitor.com",
					"password", "hola"))))
				.andExpect(status().isBadRequest());
	}
	
	
	
}
