package com.chat.yourway.service.impl;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(ChangePasswordDto request, UserDetails userDetails) {
        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            throw new OldPasswordsIsNotEqualToNewException("Old password is not correct, try again.");
        }

        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        contactRepository.changePasswordByUsername(newEncodedPassword, userDetails.getUsername());
    }
}
