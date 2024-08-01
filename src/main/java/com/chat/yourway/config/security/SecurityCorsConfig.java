package com.chat.yourway.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Component
@RequiredArgsConstructor
public class SecurityCorsConfig {

    private final SecurityCorsProperties securityCorsProperties;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(securityCorsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(securityCorsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(securityCorsProperties.getAllowedHeaders());
        configuration.setAllowCredentials(securityCorsProperties.getAllowCredentials());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
