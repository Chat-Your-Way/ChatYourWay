package com.chat.yourway.controller;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.chat.yourway.service.interfaces.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

  private final AuthenticationService authService;
  private final ActivateAccountService activateAccountService;

  @PostMapping(path = "/register",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Registration")
  public AuthResponseDto register(@Valid @RequestBody ContactRequestDto request,
                                  @RequestHeader(HttpHeaders.REFERER) String clientHost) {
    return authService.register(request, clientHost);
  }

  @PostMapping(path = "/login",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Authorization")
  public AuthResponseDto authenticate(@Valid @RequestBody AuthRequestDto request) {
    return authService.authenticate(request);
  }

  @PostMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Refresh token")
  @ApiResponse(responseCode = "401", description = "User UNAUTHORIZED")
  public AuthResponseDto refreshToken(HttpServletRequest request) {
    return authService.refreshToken(request);
  }

  @PostMapping("/activate")
  @Operation(summary = "Activate account")
  public void activateAccount(@RequestParam String token) {
    activateAccountService.activateAccount(token);
  }

}
