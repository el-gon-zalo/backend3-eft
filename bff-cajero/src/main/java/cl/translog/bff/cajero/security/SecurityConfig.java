package cl.translog.bff.cajero.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cajero/**").hasAnyAuthority("SCOPE_cajero.access", "SCOPE_admin.access")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(this::unauthorized)
                        .accessDeniedHandler(this::forbidden))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(this::unauthorized)
                        .accessDeniedHandler(this::forbidden));

        return http.build();
    }

    private void unauthorized(HttpServletRequest request, HttpServletResponse response,
                              AuthenticationException ex) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":401,\"error\":\"UNAUTHORIZED\",\"message\":\"Token requerido o inválido\"}");
    }

    private void forbidden(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException ex) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":403,\"error\":\"FORBIDDEN\",\"message\":\"Token sin permisos para este canal\"}");
    }
}