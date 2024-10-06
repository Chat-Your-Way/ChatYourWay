package com.chat.yourway.controller.rest;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.dto.response.error.ApiErrorResponseDto;
import com.chat.yourway.security.LogoutService;
import com.chat.yourway.service.ActivateAccountService;
import com.chat.yourway.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final ActivateAccountService activateAccountService;
    private final LogoutService logoutService;
    private static final String REGISTER = "/register";
    private static final String LOGIN = "/login";
    private static final String REFRESH = "/refresh";
    private static final String ACTIVATE = "/activate";
    private static final String LOGOUT = "/logout";

    @Operation(summary = "Registration a new contact", responses = {
                    @ApiResponse(responseCode = "201", description = SUCCESSFULLY_REGISTERED,
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = ERR_SENDING_EMAIL,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
                }
            )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = REGISTER, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public AuthResponseDto register(@Valid @RequestBody ContactRequestDto request,
                                    @RequestHeader(HttpHeaders.REFERER) String clientHost) {
        return authService.register(request, clientHost);
    }

    @Operation(summary = "Authorization", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_AUTHORIZATION,
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(path = LOGIN, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public AuthResponseDto authenticate(@Valid @RequestBody AuthRequestDto request) {
        return authService.authenticate(request);
    }

    @Operation(summary = "Refresh token", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REFRESHED_TOKEN,
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(path = REFRESH, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public AuthResponseDto refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

    @Operation(summary = "Activate account", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_ACTIVATED_ACCOUNT),
                    @ApiResponse(responseCode = "404", description = EMAIL_TOKEN_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(path = ACTIVATE, consumes = APPLICATION_JSON_VALUE)
    public void activateAccount(@RequestParam(name = "Email token") UUID token) {
        activateAccountService.activateAccount(token);
    }

    @Operation(summary = "Logout", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_LOGGED_OUT),
                    @ApiResponse(responseCode = "401", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(value = LOGOUT, consumes = APPLICATION_JSON_VALUE)
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        logoutService.logout(request, response, auth);
    }
}
