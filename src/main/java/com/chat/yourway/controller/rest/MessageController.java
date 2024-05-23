package com.chat.yourway.controller.rest;

import static com.chat.yourway.config.openapi.OpenApiMessages.CONTACT_UNAUTHORIZED;
import static com.chat.yourway.config.openapi.OpenApiMessages.INVALID_VALUE;
import static com.chat.yourway.config.openapi.OpenApiMessages.MESSAGE_HAS_ALREADY_REPORTED;
import static com.chat.yourway.config.openapi.OpenApiMessages.MESSAGE_NOT_FOUND;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_REPORTED_MESSAGE;
import static com.chat.yourway.config.openapi.OpenApiMessages.TOPIC_NOT_ACCESS;
import static com.chat.yourway.config.openapi.OpenApiMessages.TOPIC_NOT_FOUND;

import com.chat.yourway.dto.request.MessageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.error.ApiErrorResponseDto;
import com.chat.yourway.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Message")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Send message to topic",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SUCCESSFULLY_REPORTED_MESSAGE,
                content = @Content),
            @ApiResponse(responseCode = "400", description = INVALID_VALUE),
            @ApiResponse(
                responseCode = "403",
                description = TOPIC_NOT_ACCESS,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            @ApiResponse(
                responseCode = "403",
                description = CONTACT_UNAUTHORIZED,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
        })
    @PostMapping("/topic/{topicId}")
    public MessageResponseDto sendToPublicTopic(@PathVariable UUID topicId,
        @Valid @RequestBody MessageRequestDto message, Principal principal) {
        String email = principal.getName();
        return messageService.sendToTopic(topicId, message, email);
    }

    @Operation(summary = "Get messages by topic",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SUCCESSFULLY_REPORTED_MESSAGE,
                content = @Content),
            @ApiResponse(responseCode = "400", description = INVALID_VALUE),
            @ApiResponse(
                responseCode = "403",
                description = TOPIC_NOT_ACCESS,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            @ApiResponse(
                responseCode = "403",
                description = CONTACT_UNAUTHORIZED,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
        })
    @GetMapping(path = "/topic/{topicId}")
    public Page<MessageResponseDto> getMessagesByTopic(
        @Parameter(description = "Number of page (1..N)", required = true,
            schema = @Schema(type = "integer", defaultValue = "1")
        ) @RequestParam(defaultValue = "1") @Positive int page,
        @Parameter(description = "The size of the page to be returned", required = true,
            schema = @Schema(type = "integer", defaultValue = "12")
        ) @RequestParam(defaultValue = "30") @Positive int size,
        Principal principal,
        @PathVariable UUID topicId
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, "timestamp");
        return messageService.findAllByTopicId(topicId, pageable, principal);
    }

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
        @PathVariable int id, Principal principal) {
        String email = principal.getName();
        messageService.reportMessageById(id, email);
    }
}
