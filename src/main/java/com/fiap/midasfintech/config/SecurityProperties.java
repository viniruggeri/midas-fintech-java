package com.fiap.midasfintech.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    @Valid
    private Jwt jwt = new Jwt();

    @Valid
    private Auth auth = new Auth();

    @Valid
    private Cors cors = new Cors();

    @Valid
    private Oauth2 oauth2 = new Oauth2();

    @Getter
    @Setter
    public static class Jwt {
        @NotBlank
        private String secret;

        private String previousSecrets = "";

        @Min(1)
        private long expirationMinutes = 120;

        @Min(1)
        private long refreshExpirationMinutes = 10080;
    }

    @Getter
    @Setter
    public static class Auth {
        @Valid
        private RateLimit rateLimit = new RateLimit();

        @Getter
        @Setter
        public static class RateLimit {
            @Min(1)
            private int maxAttempts = 10;

            @Min(1)
            private int windowSeconds = 60;
        }
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Oauth2 {
        private boolean githubEnabled = false;

        private Set<String> adminGithubLogins = new HashSet<>();
    }
}
