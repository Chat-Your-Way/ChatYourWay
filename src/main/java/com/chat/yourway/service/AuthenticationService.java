package com.chat.yourway.service;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.request.EmailRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.dto.response.RegistrationResponseDto;
import com.chat.yourway.exception.InvalidCredentialsException;
import com.chat.yourway.exception.InvalidTokenException;
import com.chat.yourway.model.redis.Token;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.LogoutService;
import com.chat.yourway.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ActivateAccountService activateAccountService;
    private final AuthenticationManager authManager;
    private final ContactService contactService;
    private final LogoutService logoutService;
    private final TokenService tokenService;
    private final JwtService jwtService;

    @Transactional
    public RegistrationResponseDto register(ContactRequestDto contactRequestDto, String clientHost) {
        final var status = RegistrationResponseDto.builder().registerStatus("success full").build();
        log.trace("Started registration contact email: {}", contactRequestDto.getEmail());

        var contact = contactService.create(contactRequestDto);

        log.info("Saved registered contact {} to repository", contact.getEmail());

        activateAccountService.sendVerifyEmail(contact, clientHost);
        return status;
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

    public void activeAccountEmailCodeLink(EmailRequestDto emailRequestDto, String clientHost) {
        var contact = contactService.findByEmail(emailRequestDto.email());

        activateAccountService.sendVerifyEmail(contact, clientHost);
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

    public void activateAccount() {
        activateAccountService.activateAccount();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        logoutService.logout(request, response, auth);
    }

    private void saveContactToken(String email, String jwtToken) {
        var token = Token.builder()
                        .email(email)
                        .token(jwtToken)
                        .tokenType("BEARER")
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
