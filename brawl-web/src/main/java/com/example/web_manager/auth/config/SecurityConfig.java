package com.example.web_manager.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Define as regras de autorização na ordem correta
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas: login, callback do discord, etc.
                        .requestMatchers("/auth/**", "/").permitAll()
                        // Rotas da API: exigem autenticação
                        .requestMatchers("/api/**").authenticated()
                        // Qualquer outra rota: também exige autenticação
                        .anyRequest().authenticated()
                )
                // Configura o login via OAuth2 (Discord)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/auth/success", true)
                )
                // Define a política de sessão como STATELESS, pois usamos JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Adiciona nosso filtro JWT para rodar antes do filtro de autenticação padrão
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}