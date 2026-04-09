package com.fiap.midasfintech.security;

import com.fiap.midasfintech.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
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

        String githubLogin = (String) oauth2User.getAttributes().getOrDefault("login", "");
        Set<GrantedAuthority> authorities = new HashSet<>(oauth2User.getAuthorities());

        if (securityProperties.getOauth2().getAdminGithubLogins().contains(githubLogin)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
        }

        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "login");
    }
}
