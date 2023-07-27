package com.chat.yourway.service;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.dto.request.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * {@link AuthenticationService}
 *
 * @author Dmytro Trotsenko on 7/27/23
 */

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        var contact = Contact.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isPrivate(true)
                .role(Role.USER)
                .build();
        contactRepository.save(contact);

        var jwtToken = jwtService.generateToken(contact);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var contact = contactRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(contact);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}
