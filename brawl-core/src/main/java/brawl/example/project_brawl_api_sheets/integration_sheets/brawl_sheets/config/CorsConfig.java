package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Permite que a sua aplicação Angular (rodando em localhost:4200)
                // acesse a sua API
                registry.addMapping("/api/**") // Aplica a permissão para todos os endpoints sob /api/
                        .allowedOrigins("http://localhost:4200") // A origem do seu front-end
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}