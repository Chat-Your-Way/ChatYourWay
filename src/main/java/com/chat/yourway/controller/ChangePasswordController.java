package com.chat.yourway.controller;

import com.chat.yourway.config.openapi.OpenApiExamples;
import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.service.interfaces.ChangePasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.chat.yourway.config.openapi.OpenApiMessages.EMAIL_TOKEN_NOT_FOUND;
import static com.chat.yourway.config.openapi.OpenApiMessages.ERR_SENDING_EMAIL;
import static com.chat.yourway.config.openapi.OpenApiMessages.INVALID_OLD_PASSWORD;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_CHANGING_PASSWORD;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_RESTORED_PASSWORD;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_SEND_REQUEST_RESTORE_PASSWORD;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Change password")
@RestController
@RequestMapping("/change")
@RequiredArgsConstructor
public class ChangePasswordController {

  private final ChangePasswordService changePasswordService;

  @Operation(summary = "Change to new password",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_CHANGING_PASSWORD,
              content = @Content),
          @ApiResponse(responseCode = "400", description = INVALID_OLD_PASSWORD,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = ChangePasswordDto.class),
              examples = @ExampleObject(value = OpenApiExamples.CHANGE_PASSWORD,
                  description = "Old and new passwords"))))
  @PatchMapping(path = "/password",
      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public void changePassword(@RequestBody ChangePasswordDto request,
      @AuthenticationPrincipal UserDetails userDetails) {
    changePasswordService.changePassword(request, userDetails);
  }

  @Operation(summary = "Send email to restore password",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_SEND_REQUEST_RESTORE_PASSWORD,
              content = @Content),
          @ApiResponse(responseCode = "400", description = ERR_SENDING_EMAIL,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @PostMapping(path = "/password/email", produces = APPLICATION_JSON_VALUE)
  public void sendRequestToRestorePassword(@RequestParam String email,
                                           @RequestHeader(HttpHeaders.REFERER) String clientHost) {
    changePasswordService.sendEmailToRestorePassword(email, clientHost);
  }

  @Operation(summary = "Restore password",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_RESTORED_PASSWORD,
              content = @Content),
          @ApiResponse(responseCode = "404", description = EMAIL_TOKEN_NOT_FOUND,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @PatchMapping(path = "/password/restore", produces = APPLICATION_JSON_VALUE)
  public void restorePassword(@RequestParam String newPassword, @RequestParam String token) {
    changePasswordService.restorePassword(newPassword, token);
  }
}
