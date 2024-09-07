package com.chat.yourway.controller.rest;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;

@Tag(name = "Message")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private static final String TOPIC_ID = "/topic/{topicId}";
    private static final String PRIVATE_SEND_TO_EMAIL = "/private/{sendToEmail}";
    private static final String ID_REPORT = "/{id}/report";
    private static final String ID_READ = "/{id}/read";

    @Operation(summary = "Send message to topic", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REPORTED_MESSAGE),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE),
                    @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(TOPIC_ID)
    public MessageResponseDto sendToPublicTopic(@PathVariable UUID topicId,
                                                @Valid @RequestBody MessageRequestDto message) {
        return messageService.sendToTopic(topicId, message);
    }

    @Operation(summary = "Send message to private", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REPORTED_MESSAGE),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE),
                    @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(PRIVATE_SEND_TO_EMAIL)
    public MessageResponseDto sendToPrivateContact(@PathVariable String sendToEmail,
                                                   @Valid @RequestBody MessageRequestDto message) {
        return messageService.sendToContact(sendToEmail, message);
    }

    @Operation(summary = "Get messages by topic",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REPORTED_MESSAGE),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE),
                    @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = TOPIC_ID)
    public Page<MessageResponseDto> getMessagesByTopic(
            @Parameter(description = "Number of page (1..N)",
                        required = true,
                        schema = @Schema(type = "integer", defaultValue = "1")
            )
            @RequestParam(defaultValue = "1") @Positive int page,
            @Parameter(description = "The size of the page to be returned",
                        required = true,
                        schema = @Schema(type = "integer", defaultValue = "12")
            )
            @RequestParam(defaultValue = "30") @Positive int size, @PathVariable UUID topicId) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, "timestamp");
        return messageService.findAllByTopicId(topicId, pageable);
    }

    @Operation(summary = "Make report to message", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_REPORTED_MESSAGE),
                    @ApiResponse(responseCode = "400", description = MESSAGE_HAS_ALREADY_REPORTED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = MESSAGE_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(ID_REPORT)
    public void reportMessage(@PathVariable UUID id) {
        messageService.reportMessageById(id);
    }

    @Operation(summary = "Mark a message as read", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY,
                            content = @Content),
                    @ApiResponse(responseCode = "400", description = MESSAGE_HAS_ALREADY_REPORTED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = MESSAGE_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(ID_READ)
    public void readMessage(@PathVariable UUID id) {
        messageService.readMessage(id);
    }
}