package com.fiap.midasfintech.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Midas API")
                        .description("""
                                ## Sistema de Gestão Financeira Pessoal
                                
                                API RESTful para gerenciamento de contas e transações financeiras.
                                
                                ### Recursos disponíveis
                                - **Contas** — CRUD completo de contas financeiras
                                - **Transações** — Registro e consulta de receitas e despesas
                                
                                ### Nível de Maturidade Richardson
                                Esta API implementa o **nível 3** (HATEOAS), retornando links de navegação em todas as respostas.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Midas")
                                .email("contato@midas.com")
                                .url("https://github.com/viniruggeri/midas-fintech"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("https://midas-fintech-java.azurewebsites.net").description("Produção (Azure)"),
                        new Server().url("http://localhost:" + serverPort).description("Local")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório GitHub")
                        .url("https://github.com/viniruggeri/midas-fintech"));
    }
}