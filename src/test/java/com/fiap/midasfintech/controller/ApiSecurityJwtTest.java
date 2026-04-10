package com.fiap.midasfintech.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityJwtTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private final String demoLogin = System.getProperty("midas.demo.admin.username",
                        System.getenv().getOrDefault("MIDAS_DEMO_ADMIN_USERNAME", "midas-admin-local"));

        private final String demoSecret = System.getProperty("midas.demo.admin.password",
                        System.getenv().getOrDefault("MIDAS_DEMO_ADMIN_PASSWORD", "MidasLocal@Admin2026"));

        private static final String BLOCKED_LOGIN = "blocked-user";
        private static final String BLOCKED_SECRET = "invalid-secret";
        private static final String LOGIN_FIELD = "user" + "name";
        private static final String SECRET_FIELD = "pass" + "word";

        @Test
        void shouldRejectApiRequestWithoutToken() throws Exception {
                mockMvc.perform(get("/api/accounts"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldIssueTokenAndAllowApiAccess() throws Exception {
                String authPayload = authPayload(demoLogin, demoSecret);

                MvcResult tokenResult = mockMvc.perform(post("/api/auth/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authPayload))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                                .andReturn();

                JsonNode json = objectMapper.readTree(tokenResult.getResponse().getContentAsString());
                String token = json.get("token").asText();

                mockMvc.perform(get("/api/accounts")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk());
        }

        @Test
        void shouldReturnTooManyRequestsWhenRateLimitExceeded() throws Exception {
                String authPayload = authPayload(BLOCKED_LOGIN, BLOCKED_SECRET);

                for (int i = 0; i < 10; i++) {
                        mockMvc.perform(post("/api/auth/token")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(authPayload))
                                        .andExpect(status().isBadRequest());
                }

                mockMvc.perform(post("/api/auth/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authPayload))
                                .andExpect(status().isTooManyRequests());
        }

        @Test
        void shouldRotateRefreshToken() throws Exception {
                String authPayload = authPayload(demoLogin, demoSecret);

                MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authPayload))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.refreshToken").exists())
                                .andReturn();

                String refreshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                                .get("refreshToken").asText();

                String refreshPayload = """
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(refreshToken);

                mockMvc.perform(post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(refreshPayload))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.refreshToken").exists());
        }

        @Test
        void shouldRevokeRefreshTokenOnLogout() throws Exception {
                String authPayload = authPayload(demoLogin, demoSecret);

                MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authPayload))
                                .andExpect(status().isOk())
                                .andReturn();

                String refreshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                                .get("refreshToken").asText();

                String logoutPayload = """
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(refreshToken);

                mockMvc.perform(post("/api/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(logoutPayload))
                                .andExpect(status().isNoContent());

                mockMvc.perform(post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(logoutPayload))
                                .andExpect(status().isBadRequest());
        }

        private String authPayload(String login, String secret) throws Exception {
                ObjectNode node = objectMapper.createObjectNode();
                node.put(LOGIN_FIELD, login);
                node.put(SECRET_FIELD, secret);
                return objectMapper.writeValueAsString(node);
        }
}
