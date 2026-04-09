package com.fiap.midasfintech.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRouteAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectUnauthenticatedUserFromClientArea() throws Exception {
        mockMvc.perform(get("/cliente/dashboard"))
                .andExpect(status().isFound());
    }

    @Test
    void shouldAllowPublicHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRequireAuthenticationForMetricsEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldAllowClientInClientArea() throws Exception {
        mockMvc.perform(get("/cliente/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldBlockClientFromAdminArea() throws Exception {
        mockMvc.perform(get("/admin/painel"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminInAdminArea() throws Exception {
        mockMvc.perform(get("/admin/painel"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminInMetricsEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }
}
