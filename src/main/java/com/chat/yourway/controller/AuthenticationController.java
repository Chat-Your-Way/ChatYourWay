package com.chat.yourway.controller;

import static com.chat.yourway.config.openapi.OpenApiMessages.CONTACT_NOT_FOUND;
import static com.chat.yourway.config.openapi.OpenApiMessages.CONTACT_UNAUTHORIZED;
import static com.chat.yourway.config.openapi.OpenApiMessages.EMAIL_TOKEN_NOT_FOUND;
import static com.chat.yourway.config.openapi.OpenApiMessages.ERR_SENDING_EMAIL;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_ACTIVATED_ACCOUNT;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_AUTHORIZATION;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_REFRESHED_TOKEN;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_REGISTERED;
import static com.chat.yourway.config.openapi.OpenApiMessages.VALUE_NOT_UNIQUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.chat.yourway.config.openapi.OpenApiExamples;
import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.chat.yourway.service.interfaces.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

  private final AuthenticationService authService;
  private final ActivateAccountService activateAccountService;

  @Operation(summary = "Registration a new contact",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REGISTERED,
              content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
          @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "400", description = ERR_SENDING_EMAIL,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = ContactRequestDto.class),
              examples = @ExampleObject(value = OpenApiExamples.NEW_CONTACT,
                  description = "New Contact for registration"))))
  @PostMapping(path = "/register", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  public AuthResponseDto register(@Valid @RequestBody ContactRequestDto request,
                                  @RequestHeader(HttpHeaders.REFERER) String clientHost) {
    return authService.register(request, clientHost);
  }

  @Operation(summary = "Authorization",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_AUTHORIZATION,
              content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
          @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "401", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = AuthRequestDto.class),
              examples = @ExampleObject(value = OpenApiExamples.LOGIN,
                  description = "Login credentials"))))
  @PostMapping(path = "/login", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  public AuthResponseDto authenticate(@Valid @RequestBody AuthRequestDto request) {

    return authService.authenticate(request);
  }

  @Operation(summary = "Refresh token",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REFRESHED_TOKEN,
              content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
          @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "401", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @PostMapping(path = "/refresh", produces = APPLICATION_JSON_VALUE)
  public AuthResponseDto refreshToken(HttpServletRequest request) {
    return authService.refreshToken(request);
  }

  @Operation(summary = "Activate account",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_ACTIVATED_ACCOUNT,
              content = @Content),
          @ApiResponse(responseCode = "404", description = EMAIL_TOKEN_NOT_FOUND,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @PostMapping(path = "/activate", produces = APPLICATION_JSON_VALUE)
  public void activateAccount(@RequestParam(name = "Email token") String token) {
    activateAccountService.activateAccount(token);
  }

}
