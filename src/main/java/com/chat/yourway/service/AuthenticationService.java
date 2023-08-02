package com.chat.yourway.service;

import com.chat.yourway.model.*;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.repository.ContactTokenRepository;
import com.chat.yourway.repository.TokenRepository;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.dto.request.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

import static com.chat.yourway.model.EmailMessageConstant.*;
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
    private final TokenRepository tokenRepository;
    private final EmailSenderService emailSenderService;
    private final ContactTokenRepository contactTokenRepository;

    @Value("${security.jwt.token-type}")
    private String tokenType;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request, HttpServletRequest httpRequest) {
        log.info("Started registration contact email: {}", request.getEmail());
        var contact = Contact.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(false)
                .isPrivate(true)
                .role(Role.USER)
                .build();

        contact = contactRepository.save(contact);

        String uuid = UUID.randomUUID().toString();
        String link = generateLink(httpRequest, uuid, EmailMessageType.VERIFY);
        ContactToken contactToken = ContactToken.builder()
                .contact(contact)
                .token(uuid)
                .messageType(EmailMessageType.VERIFY)
                .build();

        contactTokenRepository.save(contactToken);

        sendVerifyEmail(contact, link);

        log.info("Saved registered contact to repository");
        return AuthResponseDto.builder()
                .accessToken(jwtService.generateAccessToken(contact))
                .refreshToken(jwtService.generateRefreshToken(contact))
                .build();
    }

    @Transactional
    public AuthResponseDto authenticate(AuthRequestDto request) {
        log.info("Started authenticate contact email: {}", request.getEmail());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var contact = contactRepository.findByEmail(request.getEmail())
                .orElseThrow();

        log.info("Contact authenticated");
        return AuthResponseDto.builder()
                .accessToken(jwtService.generateAccessToken(contact))
                .refreshToken(jwtService.generateRefreshToken(contact))
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

            if (jwtService.isAccessTokenValid(refreshToken, contact)) {
                var accessToken = jwtService.generateAccessToken(contact);
                revokeAllContactTokens(contact);
                saveContactToken(contact, accessToken);

                log.info("Refreshed access token for contact email: {}", contact.getEmail());
                return ResponseEntity.ok(AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
            }
        }
        return ResponseEntity.status(UNAUTHORIZED).build();
    }

    private void revokeAllContactTokens(Contact contact) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(contact.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveContactToken(Contact contact, String jwtToken) {
        var token = Token.builder()
                .contact(contact)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private String generateLink(HttpServletRequest httpRequest, String uuid, EmailMessageType emailMessageType) {
        log.info("Generate link for verifying account");
        return httpRequest.getHeader(HttpHeaders.REFERER) +
                emailMessageType.getEmailType() +
                TOKEN_PARAMETER +
                uuid;
    }

    private void sendVerifyEmail(Contact contact, String link) {
        String text = String.format(VERIFY_ACCOUNT_TEXT, contact.getUsername(), link);
        EmailSend emailSend = new EmailSend(contact.getEmail(), VERIFY_ACCOUNT_SUBJECT, text);

        emailSenderService.sendEmail(emailSend);
        log.info("Email for verifying account sent");
    }
}
