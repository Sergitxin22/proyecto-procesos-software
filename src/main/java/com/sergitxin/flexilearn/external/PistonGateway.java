package com.sergitxin.flexilearn.external;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class PistonGateway {

    private final String API_URL = "http://localhost:2000/api/v2/";

    private final HttpClient httpClient;

    public PistonGateway() {
        this.httpClient = HttpClient.newHttpClient();
    }

	public String execute(String code) {
		String url = API_URL + "execute";

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("language", "java");
        root.put("version", "15.0.2");

        ObjectNode file = mapper.createObjectNode();
        file.put("name", "Main.java");
        file.put("content", code);

        ArrayNode files = mapper.createArrayNode();
        files.add(file);

        root.set("files", files);

        String json = "";
        try {
            json = mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);
		
		try {
			HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:2000/api/v2/execute"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			 
			if (response.statusCode() == 200) {
                mapper = new ObjectMapper();
                JsonNode responseRoot = mapper.readTree(response.body());

                String stdout = responseRoot.path("run").path("stdout").asText();
                System.out.println(stdout);
                return stdout;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}