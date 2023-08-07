package com.chat.yourway.service;

import com.chat.yourway.dto.request.ChangePasswordDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface ContactService {
    void changePassword(ChangePasswordDto request, UserDetails userDetails);
}
