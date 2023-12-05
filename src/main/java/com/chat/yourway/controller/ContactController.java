package com.chat.yourway.controller;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.chat.yourway.config.openapi.OpenApiExamples;
import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.dto.response.ContactProfileResponseDto;
import com.chat.yourway.service.interfaces.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Contact")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

  private final ContactService contactService;

  @Operation(summary = "Edit contact profile",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UPDATED_CONTACT_PROFILE),
          @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = EditContactProfileRequestDto.class),
              examples = @ExampleObject(value = OpenApiExamples.EDIT_CONTACT_PROFILE,
                  description = "Edit Contact profile"))))
  @PatchMapping(path = "/profile", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public void editContactProfile(
      @Valid @RequestBody EditContactProfileRequestDto editContactProfileRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {
    contactService.updateContactProfile(editContactProfileRequestDto, userDetails);
  }

  @Operation(summary = "Get contact profile",
          responses = {
                  @ApiResponse(responseCode = "200", description = SUCCESSFULLY_RECEIVED_CONTACT_PROFILE),
                  @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                  @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
          })
  @GetMapping(path = "/profile", produces = APPLICATION_JSON_VALUE)
  public ContactProfileResponseDto getContactProfile(
          @AuthenticationPrincipal UserDetails userDetails) {
    return contactService.getContactProfile(userDetails);
  }

  @Operation(
          summary = "Prohibit sending private message",
          responses = {
                  @ApiResponse(responseCode = "200", description = SUCCESSFULLY_PROHIBITED_SENDING_PRIVATE_MESSAGES),
                  @ApiResponse(
                          responseCode = "403",
                          description = CONTACT_UNAUTHORIZED,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                  @ApiResponse(
                          responseCode = "404",
                          description = CONTACT_NOT_FOUND,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
          })
  @PatchMapping(path = "/message/send/prohibit")
  public void prohibitSendingPrivateMessages(@AuthenticationPrincipal UserDetails userDetails) {
    contactService.prohibitSendingPrivateMessages(userDetails);
  }

  @Operation(
          summary = "Permit sending private message",
          responses = {
                  @ApiResponse(responseCode = "200", description = SUCCESSFULLY_PERMITTED_SENDING_PRIVATE_MESSAGES),
                  @ApiResponse(
                          responseCode = "403",
                          description = CONTACT_UNAUTHORIZED,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                  @ApiResponse(
                          responseCode = "404",
                          description = CONTACT_NOT_FOUND,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
          })
  @PatchMapping(path = "/message/send/permit")
  public void permitSendingPrivateMessages(@AuthenticationPrincipal UserDetails userDetails) {
    contactService.permitSendingPrivateMessages(userDetails);
  }
}
