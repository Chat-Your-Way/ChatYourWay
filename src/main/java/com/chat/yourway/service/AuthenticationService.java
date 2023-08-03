package com.chat.yourway.service;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.model.Token;
import com.chat.yourway.model.TokenType;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.repository.TokenRedisRepository;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.dto.request.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

/**
 * {@link AuthenticationService}
 *
 * @author Dmytro Trotsenko on 7/27/23
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final TokenRedisRepository tokenRedisRepository;

    @Value("${security.jwt.token-type}")
    private String tokenType;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        log.info("Started registration contact email: {}", request.getEmail());
        var contact = Contact.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isPrivate(true)
                .role(Role.USER)
                .build();

        contactRepository.save(contact);

        var accessToken = jwtService.generateAccessToken(contact);
        var refreshToken = jwtService.generateRefreshToken(contact);

        saveContactToken(contact.getEmail(), accessToken);

        log.info("Saved registered contact to repository");
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponseDto authenticate(AuthRequestDto request) {
        log.info("Started authenticate contact email: {}", request.getEmail());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var contact = contactRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var accessToken = jwtService.generateAccessToken(contact);
        var refreshToken = jwtService.generateRefreshToken(contact);

        saveContactToken(contact.getEmail(), accessToken);

        log.info("Contact authenticated");
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String tokenTypePrefix = tokenType + " ";

        log.info("Started refresh token by authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith(tokenTypePrefix)) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        final String refreshToken = authHeader.substring(tokenTypePrefix.length());
        final String email = jwtService.extractEmail(refreshToken);

        if (email != null) {
            var contact = this.contactRepository.findByEmail(email)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, contact)) {
                var accessToken = jwtService.generateAccessToken(contact);
                revokeAllContactTokens(contact);
                saveContactToken(email, accessToken);

                log.info("Refreshed access token for contact email: {}", email);
                return ResponseEntity.ok(AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
            }
        }
        return ResponseEntity.status(UNAUTHORIZED).build();
    }

    private void revokeAllContactTokens(Contact contact) {
        var validUserTokens = tokenRedisRepository.findAllByEmail(contact.getEmail());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRedisRepository.saveAll(validUserTokens);
    }

    private void saveContactToken(String email, String jwtToken) {
        var token = Token.builder()
                .email(email)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRedisRepository.save(token);
    }

}
