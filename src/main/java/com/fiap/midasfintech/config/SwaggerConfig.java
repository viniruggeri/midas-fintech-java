package com.fiap.midasfintech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Midas API")
                        .description("Sistema de gestão financeira pessoal")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Midas")
                                .email("contato@midas.com")
                                .url("https://github.com/viniruggeri/midas-fintech"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}