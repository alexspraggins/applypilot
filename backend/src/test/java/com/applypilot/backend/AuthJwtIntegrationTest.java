package com.applypilot.backend;

import com.applypilot.backend.repository.ApplicationRepository;
import com.applypilot.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
class AuthJwtIntegrationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void cleanDatabase() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        applicationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerReturnsJwtToken() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Test User",
                "email", "test@example.com",
                "password", "password123"
        ));

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertNotNull(json.get("token"));
        assertTrue(!json.get("token").asText().isBlank());
    }

    @Test
    void loginReturnsJwtTokenForExistingUser() throws Exception {
        registerUser("Test User", "test@example.com", "password123");

        String body = objectMapper.writeValueAsString(Map.of(
                "email", "test@example.com",
                "password", "password123"
        ));

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertNotNull(json.get("token"));
        assertTrue(!json.get("token").asText().isBlank());
    }

    @Test
    void applicationsAreProtectedAndOwnedByJwtUser() throws Exception {
        String tokenOne = registerUser("User One", "user1@example.com", "password123");

        int unauthenticatedStatus = mockMvc.perform(get("/api/applications"))
                .andReturn()
                .getResponse()
                .getStatus();
        assertTrue(unauthenticatedStatus == 401 || unauthenticatedStatus == 403);

        String createBody = objectMapper.writeValueAsString(applicationRequest(
                "OpenAI",
                "Backend Engineer",
                "Remote",
                "https://example.com/jobs/backend",
                "APPLIED",
                "2026-05-21",
                "Testing JWT-owned applications"
        ));

        String createResponse = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenOne)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("OpenAI"))
                .andExpect(jsonPath("$.position").value("Backend Engineer"))
                .andExpect(jsonPath("$.userId").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = objectMapper.readTree(createResponse);
        long applicationId = created.get("id").asLong();
        long userId = created.get("userId").asLong();

        mockMvc.perform(get("/api/applications")
                        .header("Authorization", "Bearer " + tokenOne))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(applicationId))
                .andExpect(jsonPath("$[0].company").value("OpenAI"))
                .andExpect(jsonPath("$[0].userId").value(userId));

        String tokenTwo = registerUser("User Two", "user2@example.com", "password456");

        mockMvc.perform(get("/api/applications/" + applicationId)
                        .header("Authorization", "Bearer " + tokenTwo))
                .andExpect(status().isNotFound());

        String updateBody = objectMapper.writeValueAsString(applicationRequest(
                "OpenAI",
                "Senior Backend Engineer",
                "Remote",
                "https://example.com/jobs/backend",
                "INTERVIEWING",
                "2026-05-21",
                "Updated after JWT test"
        ));

        mockMvc.perform(put("/api/applications/" + applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenTwo)
                        .content(updateBody))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/applications/" + applicationId)
                        .header("Authorization", "Bearer " + tokenTwo))
                .andExpect(status().isNotFound());
    }

    private String registerUser(String name, String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", name,
                "email", email,
                "password", password
        ));

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    private Map<String, Object> applicationRequest(
            String company,
            String position,
            String location,
            String jobUrl,
            String status,
            String appliedDate,
            String notes
    ) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("company", company);
        request.put("position", position);
        request.put("location", location);
        request.put("jobUrl", jobUrl);
        request.put("status", status);
        request.put("appliedDate", appliedDate);
        request.put("notes", notes);
        return request;
    }
}
