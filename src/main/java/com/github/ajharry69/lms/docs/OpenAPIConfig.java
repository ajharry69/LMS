package com.github.ajharry69.lms.docs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Harrison",
                        email = "oharry0535@gmail.com"
                ),
                description = "OpenAPI documentation",
                title = "OpenAPI specification - LMS",
                version = "v1"
        ),
        servers = {
                @Server(
                        description = "Development",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Ngrok",
                        url = "https://fdda-105-163-2-85.ngrok-free.app/"
                )
        }
)
public class OpenAPIConfig {
}
