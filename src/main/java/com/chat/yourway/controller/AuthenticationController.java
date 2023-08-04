package com.chat.yourway.controller;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.dto.request.RegisterRequestDto;
import com.chat.yourway.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * {@link AuthenticationController}
 *
 * @author Dmytro Trotsenko on 7/26/23
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @Operation(summary = "Registration")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.register(request, httpRequest));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authorization")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, schema = @Schema(type = "string"))
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate account")
    public void activateAccount(@RequestParam String token) {
        authService.activateAccount(token);
    }

}
