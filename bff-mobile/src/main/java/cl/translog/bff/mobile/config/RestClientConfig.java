package cl.translog.bff.mobile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

/** Configura el cliente HTTP que apunta al CORE y reenvía el token del cliente. */
@Configuration
public class RestClientConfig {

    @Bean
    RestClient coreStreamingRestClient(
            @Value("${core-streaming.base-url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth instanceof JwtAuthenticationToken jwt) {
                        request.getHeaders().setBearerAuth(jwt.getToken().getTokenValue());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}