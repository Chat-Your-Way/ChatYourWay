package com.chat.yourway.service;

import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.model.*;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.repository.TokenRedisRepository;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.dto.request.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

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
    private final EmailMessageFactoryService emailMessageFactoryService;
    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailSenderService emailSenderService;
    private final EmailTokenRepository emailTokenRepository;
    private final TokenRedisRepository tokenRedisRepository;


    @Value("${security.jwt.token-type}")
    private String tokenType;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request, HttpServletRequest httpRequest) {
        log.info("Started registration contact email: {}", request.getEmail());
        var username = request.getUsername();
        var email = request.getEmail();
        var encodedPassword = passwordEncoder.encode(request.getPassword());
        var contact = Contact.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .isActive(false)
                .isPrivate(true)
                .role(Role.USER)
                .build();

        contactRepository.save(contact);

        var uuid = UUID.randomUUID().toString();
        var path = httpRequest.getHeader(REFERER);
        var emailMessageInfoDto = new EmailMessageInfoDto(username, email, uuid, path, EmailMessageType.ACTIVATE);
        var emailMessage = emailMessageFactoryService.generateEmailMessage(emailMessageInfoDto);
        var emailToken = EmailToken.builder()
                .contact(contact)
                .token(uuid)
                .messageType(EmailMessageType.ACTIVATE)
                .build();

        emailTokenRepository.save(emailToken);
        emailSenderService.sendEmail(emailMessage);

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

        var contact = contactRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), contact.getPassword())) {
            throw new OldPasswordsIsNotEqualToNewException("Password is not correct, try again.");
        }

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

    @Transactional
    public void activateAccount(String token) {
        EmailToken emailToken = emailTokenRepository.findById(token).orElseThrow();
        Contact contact = emailToken.getContact();

        contact.setIsActive(true);
        emailTokenRepository.delete(emailToken);
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
