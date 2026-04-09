package com.fiap.midasfintech.config;

import com.fiap.midasfintech.entity.AppUser;
import com.fiap.midasfintech.entity.Role;
import com.fiap.midasfintech.repository.AppUserRepository;
import com.fiap.midasfintech.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByNome("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(createRole("ROLE_ADMIN")));

        Role clientRole = roleRepository.findByNome("ROLE_CLIENT")
                .orElseGet(() -> roleRepository.save(createRole("ROLE_CLIENT")));

        String adminUsername = getPropertyOrDefault("midas.demo.admin.username", "midas-admin");
        String adminPassword = getPropertyOrDefault("midas.demo.admin.password", "midas-change-me-admin");

        String clientUsername = getPropertyOrDefault("midas.demo.client.username", "midas-client");
        String clientPassword = getPropertyOrDefault("midas.demo.client.password", "midas-change-me-client");

        appUserRepository.findByUsername(adminUsername)
                .orElseGet(() -> appUserRepository.save(createUser(adminUsername, adminPassword, Set.of(adminRole))));

        appUserRepository.findByUsername(clientUsername)
                .orElseGet(
                        () -> appUserRepository.save(createUser(clientUsername, clientPassword, Set.of(clientRole))));
    }

    private Role createRole(String nome) {
        Role role = new Role();
        role.setNome(nome);
        return role;
    }

    private AppUser createUser(String username, String plainPassword, Set<Role> roles) {
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(plainPassword));
        appUser.setEnabled(true);
        appUser.setRoles(roles);
        return appUser;
    }

    private String getPropertyOrDefault(String key, String fallback) {
        String value = environment.getProperty(key);
        return StringUtils.hasText(value) ? value : fallback;
    }
}
