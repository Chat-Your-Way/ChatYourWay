package com.chat.yourway.service;

import static com.chat.yourway.model.token.TokenType.*;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.exception.InvalidCredentialsException;
import com.chat.yourway.exception.InvalidTokenException;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ContactService contactService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final ActivateAccountService activateAccountService;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthResponseDto register(ContactRequestDto contactRequestDto, String clientHost) {
        log.trace("Started registration contact email: {}", contactRequestDto.getEmail());

        var contact = contactService.create(contactRequestDto);
        activateAccountService.sendVerifyEmail(contact, clientHost);

        var accessToken = jwtService.generateAccessToken(contact);
        var refreshToken = jwtService.generateRefreshToken(contact);

        saveContactToken(contact.getEmail(), accessToken);
        log.info("Saved registered contact {} to repository", contact.getEmail());

        return AuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Transactional
    public AuthResponseDto authenticate(AuthRequestDto authRequestDto) {
        log.trace("Started authenticate contact email: {}", authRequestDto.getEmail());
        authenticateCredentials(authRequestDto.getEmail(), authRequestDto.getPassword());

        var contact = contactService.findByEmail(authRequestDto.getEmail());

        contactService.verifyPassword(authRequestDto.getPassword(), contact.getPassword());

        var accessToken = jwtService.generateAccessToken(contact);
        var refreshToken = jwtService.generateRefreshToken(contact);

        saveContactToken(contact.getEmail(), accessToken);

        log.info("Contact {} authenticated", contact.getEmail());
        return AuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Transactional
    public AuthResponseDto refreshToken(HttpServletRequest request) {
        log.trace("Started refreshing access token");
        final String refreshToken = jwtService.extractToken(request);
        final String email = jwtService.extractEmail(refreshToken);

        var contact = contactService.findByEmail(email);

        if (!jwtService.isTokenValid(refreshToken, contact)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        var accessToken = jwtService.generateAccessToken(contact);
        tokenService.revokeAllContactTokens(contact);
        saveContactToken(email, accessToken);

        log.info("Refreshed access token for contact email: {}", email);
        return AuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private void saveContactToken(String email, String jwtToken) {
        var token =
                Token.builder()
                        .email(email)
                        .token(jwtToken)
                        .tokenType(BEARER)
                        .expired(false)
                        .revoked(false)
                        .build();
        tokenService.saveToken(token);
    }

    private void authenticateCredentials(String email, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Authentication failed, invalid email or password");
        }
    }
}
