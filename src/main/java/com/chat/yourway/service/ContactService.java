package com.chat.yourway.service;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import org.springframework.security.core.userdetails.UserDetails;

public interface ContactService {
    /**
     * Changes the password for the user's account based on the provided request and user details.
     *
     * @param request      The data transfer object (DTO) containing the necessary information for password change.
     * @param userDetails The details of the user account for which the password change is requested.
     * @throws OldPasswordsIsNotEqualToNewException If old password does not matched to current user password.
     */
    void changePassword(ChangePasswordDto request, UserDetails userDetails);
}
