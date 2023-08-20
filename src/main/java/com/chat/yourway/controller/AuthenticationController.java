package com.chat.yourway.controller;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.service.ActivateAccountServiceImpl;
import com.chat.yourway.service.AuthenticationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

  private final AuthenticationServiceImpl authService;
  private final ActivateAccountServiceImpl activateAccountServiceImpl;

  @PostMapping(path = "/register",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Registration")
  public AuthResponseDto register(@RequestBody ContactRequestDto request,
      HttpServletRequest httpRequest) {
    return authService.register(request, httpRequest);
  }

  @PostMapping(path = "/login",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Authorization")
  public AuthResponseDto authenticate(@RequestBody AuthRequestDto request) {
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
    activateAccountServiceImpl.activateAccount(token);
  }

}
