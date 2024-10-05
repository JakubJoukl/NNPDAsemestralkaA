package com.example.semestralkaa.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi(){
        SecurityScheme securityScheme = new SecurityScheme()
                .type(HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .description("Enter 'Bearer' [space] and then your token in the text input below. \n\n Example: `Bearer your_token_here`");

        return new OpenAPI()
                .info(new Info().title("My API - semestralka A")
                        .version("1.0")
                        .description("API for semestralka A"))
                .addServersItem(new Server().url("http://localhost:8080"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}