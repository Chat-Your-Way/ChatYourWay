package com.chat.yourway.config.security;

import com.chat.yourway.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;

  private static final String[] WHITELIST_URLS = {
      //OpenApi
      "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
      "/swagger-resources/**",
      "/configuration/ui", "/configuration/security", "/swagger-ui/**", "/webjars/**",
      "/swagger-ui.html",

      //Authentication
      "/auth/**",

      //WebSocket
      "/ws/**", "/chat/**",

      //Other
      "/"
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(WHITELIST_URLS).permitAll()
            .requestMatchers("/demo-controller").hasAuthority("USER")
            .anyRequest().authenticated()
        )
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .formLogin(AbstractAuthenticationFilterConfigurer::permitAll
        )
        .logout(logout -> logout
            .logoutUrl("/auth/logout")
            .addLogoutHandler(logoutHandler)
            .logoutSuccessHandler(
                (request, response, authentication) -> SecurityContextHolder.clearContext())
        )
        .build();
  }

}
