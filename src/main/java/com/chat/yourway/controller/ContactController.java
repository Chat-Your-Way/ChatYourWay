package com.chat.yourway.controller;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
@Tag(name = "Contact")
public class ContactController {

    private final ContactService contactService;

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change to new password")
    public void changePassword(@RequestBody ChangePasswordDto request, @AuthenticationPrincipal UserDetails userDetails) {
        contactService.changePassword(request, userDetails);
    }
}
