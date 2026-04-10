package com.fiap.midasfintech.security;

import com.fiap.midasfintech.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security.oauth2", name = "github-enabled", havingValue = "true")
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final SecurityProperties securityProperties;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        return applyRolePolicy(oauth2User);
    }

    OAuth2User applyRolePolicy(OAuth2User oauth2User) {
        String githubLogin = ((String) oauth2User.getAttributes().getOrDefault("login", "")).trim();
        Set<GrantedAuthority> authorities = new HashSet<>(oauth2User.getAuthorities());

        boolean isGithubAdmin = securityProperties.getOauth2().getAdminGithubLogins().stream()
                .anyMatch(login -> login != null && login.equalsIgnoreCase(githubLogin));

        if (!isGithubAdmin) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("unauthorized_github_user"),
                    "Acesso GitHub permitido apenas para logins administrativos autorizados.");
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "login");
    }
}
