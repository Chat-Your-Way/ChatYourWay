package com.chat.yourway.security;

import com.chat.yourway.repository.TokenRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * {@link LogoutService}
 *
 * @author Dmytro Trotsenko on 8/2/23
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRedisRepository tokenRedisRepository;
    @Value("${security.jwt.token-type}")
    private String tokenType;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String tokenTypePrefix = tokenType + " ";

        log.info("Started logout by authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith(tokenTypePrefix)) {
            log.info("Logout Header invalid");
            return;
        }

        final String refreshToken = authHeader.substring(tokenTypePrefix.length());

        var storedRefreshToken = tokenRedisRepository.findByToken(refreshToken)
                .orElse(null);

        if (storedRefreshToken != null) {
            storedRefreshToken.setExpired(true);
            storedRefreshToken.setRevoked(true);
            tokenRedisRepository.save(storedRefreshToken);
            SecurityContextHolder.clearContext();
            log.info("Logout for contact email: {}", storedRefreshToken.email);
        } else {
            log.info("storedRefreshToken == null");
        }
    }
}
