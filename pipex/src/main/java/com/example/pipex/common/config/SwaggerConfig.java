package com.example.pipex.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

        private static final String SECURITY_SCHEME_NAME = "ChatwootAuth";

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**", "/v3/api-docs/**")
                                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                                .setCachePeriod(0);
        }

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                                                new SecurityScheme()
                                                                                .name("authorization")
                                                                                .type(SecurityScheme.Type.APIKEY)
                                                                                .in(SecurityScheme.In.HEADER)
                                                                                .description("Chatwoot access token for authentication")))
                                .info(new Info()
                                                .title("CRM Service API")
                                                .version("1.0.1")
                                                .description("API documentation for PipeX")
                                                .contact(new Contact()
                                                                .name("PipeX Service")
                                                                .email("support@pipex.com")
                                                                .url("https://pipex.com/"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8081/api")
                                                                .description("Development Server"),
                                                new Server()
                                                                .url("https://api.chatcore.com")
                                                                .description("Production Server")));
        }
}
