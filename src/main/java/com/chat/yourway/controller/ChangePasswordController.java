package com.chat.yourway.controller;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.service.interfaces.ChangePasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Change password")
@RestController
@RequestMapping("/change")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    @Operation(summary = "Change to new password")
    @PatchMapping(path = "/password",
            consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody ChangePasswordDto request,
                               @AuthenticationPrincipal UserDetails userDetails) {
        changePasswordService.changePassword(request, userDetails);
    }

    @Operation(summary = "Send email to restore password")
    @PostMapping(path = "/password/email")
    public void sendRequestToRestorePassword(@RequestParam String email,
                                             @RequestHeader(HttpHeaders.REFERER) String clientHost) {
        changePasswordService.sendEmailToRestorePassword(email, clientHost);
    }

    @Operation(summary = "Restore password")
    @PatchMapping(path = "/password/restore")
    public void restorePassword(@RequestParam String newPassword,
                                @RequestParam String token) {
        changePasswordService.restorePassword(newPassword, token);
    }
}
