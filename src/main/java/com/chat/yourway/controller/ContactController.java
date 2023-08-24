package com.chat.yourway.controller;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Contact")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "Change to new password")
    @PatchMapping(path = "/password",
            consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody ChangePasswordDto request,
                               @AuthenticationPrincipal UserDetails userDetails) {
        contactService.changePassword(request, userDetails);
    }

    @Operation(summary = "Send email to restore password")
    @PostMapping(path = "/password/email")
    public void sendRequestToRestorePassword(@RequestParam String email,
                                             @RequestHeader(HttpHeaders.REFERER) String clientAddress) {
        contactService.sendEmailToRestorePassword(email, clientAddress);
    }

    @Operation(summary = "Restore password")
    @PatchMapping(path = "/password/restore")
    public void restorePassword(@RequestParam String newPassword,
                                @RequestParam String token) {
        contactService.restorePassword(newPassword, token);
    }
}
