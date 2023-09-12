package com.chat.yourway.controller;

import com.chat.yourway.config.openapi.OpenApiExamples;
import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.service.interfaces.ContactService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Contact")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final TopicSubscriberService topicSubscriberService;

    @Operation(summary = "Registration a new contact",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UPDATED_CONTACT_PROFILE),
                    @ApiResponse(responseCode = "404", description = CONTACT_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = EditContactProfileRequestDto.class),
                            examples = @ExampleObject(value = OpenApiExamples.EDIT_CONTACT_PROFILE,
                                    description = "Edit Contact profile"))))
    @PatchMapping(path = "/profile", consumes = APPLICATION_JSON_VALUE)
    public void editContactProfile(@Valid @RequestBody EditContactProfileRequestDto editContactProfileRequestDto,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        contactService.updateContactProfile(editContactProfileRequestDto, userDetails);
    }

    @PostMapping(path = "/subscribe/{topicId}")
    public void subscribeToTopic(@PathVariable Integer topicId, Principal principal) {
        String email = principal.getName();
        topicSubscriberService.subscribeToTopic(email, topicId);
    }

    @PostMapping(path = "/unsubscribe/{topicId}")
    public void unsubscribeFromTopic(@PathVariable Integer topicId, Principal principal){
        String email = principal.getName();
        topicSubscriberService.unsubscribeFromTopic(email, topicId);
    }

}
