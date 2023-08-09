package com.chat.yourway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Dmytro Trotsenko",
            email = "trohae7@gmail.com"
        ),
        description = "OpenApi documentation for project 'Chat Your Way'",
        title = "OpenApi specification - Chat Your Way",
        version = "1.0",
        termsOfService = "Terms of service"
    ),
    servers = {
        @Server(
            description = "Local ENV",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "PROD ENV",
            url = "https://some-url.com"
        )
    },
    security = {
        @SecurityRequirement(
            name = "bearerAuth"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
