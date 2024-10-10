package com.chat.yourway.controller.rest;

import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.dto.response.ContactProfileResponseDto;
import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.dto.response.error.ApiErrorResponseDto;
import com.chat.yourway.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Contact")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private static final String PROFILE = "/profile";
    private static final String GET_PROFILE = "/profile";
    private static final String MESSAGE_SEND_PROHIBIT = "/message/send/prohibit";
    private static final String MESSAGE_SEND_PERMIT = "/message/send/permit";
    private static final String ONLINE_TOPIC_ID = "/online/{topic-id}";
    private static final String ONLINE = "/online";

    @Operation(summary = "Edit contact profile", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UPDATED_CONTACT_PROFILE),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PatchMapping(path = PROFILE, consumes = APPLICATION_JSON_VALUE)
    public void editContactProfile(
            @Valid @RequestBody EditContactProfileRequestDto editContactProfileRequestDto) {
        contactService.updateContactProfile(editContactProfileRequestDto);
    }

    @Operation(summary = "Get contact profile", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_RECEIVED_CONTACT_PROFILE,
                            content = @Content(schema = @Schema(implementation = ContactProfileResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403",description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = GET_PROFILE, produces = APPLICATION_JSON_VALUE)
    public ContactProfileResponseDto getContactProfile() {
        return contactService.getContactProfile();
    }

    @Operation(summary = "Prohibit sending private message", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_PROHIBITED_SENDING_PRIVATE_MESSAGES),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PatchMapping(path = MESSAGE_SEND_PROHIBIT, consumes = APPLICATION_JSON_VALUE)
    public void prohibitSendingPrivateMessages() {
        contactService.prohibitSendingPrivateMessages();
    }

    @Operation(summary = "Permit sending private message", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_PERMITTED_SENDING_PRIVATE_MESSAGES),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PatchMapping(path = MESSAGE_SEND_PERMIT, consumes = APPLICATION_JSON_VALUE)
    public void permitSendingPrivateMessages() {
        contactService.permitSendingPrivateMessages();
    }

    @Operation(summary = "Find all online contacts by topic id", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = ONLINE_TOPIC_ID, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public List<ContactResponseDto> findAllOnlineContactsByTopicId(@PathVariable("topic-id") UUID topicId) {
        return contactService.findAllOnlineContactsByTopicId(topicId);
    }

    @Operation(summary = "Find all online contacts", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = ONLINE, produces = APPLICATION_JSON_VALUE)
    public List<ContactResponseDto> findAllOnlineContacts() {
        return contactService.findAllOnlineContacts();
    }
}
