package com.fiap.midasfintech.security;

import com.fiap.midasfintech.config.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GithubOAuth2UserServiceTest {

    @Test
    void deveNegarLoginGithubQuandoNaoForAdminAutorizado() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.getOauth2().getAdminGithubLogins().add("admin-liberado");
        GithubOAuth2UserService service = new GithubOAuth2UserService(securityProperties);

        assertThrows(OAuth2AuthenticationException.class,
                () -> service.applyRolePolicy(criarOAuth2User("usuario-comum")));
    }

    @Test
    void deveConcederRoleAdminQuandoLoginGithubEstiverAutorizado() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.getOauth2().getAdminGithubLogins().add("Admin-Liberado");
        GithubOAuth2UserService service = new GithubOAuth2UserService(securityProperties);

        OAuth2User usuarioComRole = service.applyRolePolicy(criarOAuth2User("admin-liberado"));
        boolean possuiAdmin = usuarioComRole.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        assertTrue(possuiAdmin);
    }

    private OAuth2User criarOAuth2User(String login) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", login);
        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("OAUTH2_USER")),
                attributes,
                "login");
    }
}
