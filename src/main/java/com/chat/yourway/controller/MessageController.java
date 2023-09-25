package com.chat.yourway.controller;

import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.service.interfaces.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;

@Tag(name = "Message")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
  private final MessageService messageService;

  @Operation(summary = "Make report to message",
          responses = {
                  @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REPORTED_MESSAGE,
                          content = @Content),
                  @ApiResponse(responseCode = "400", description = MESSAGE_HAS_ALREADY_REPORTED,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                  @ApiResponse(responseCode = "404", description = MESSAGE_NOT_FOUND,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
          })
  @PostMapping("/{id}/report")
  public void reportMessage(
      @PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
    messageService.reportMessageById(id, userDetails);
  }
}
