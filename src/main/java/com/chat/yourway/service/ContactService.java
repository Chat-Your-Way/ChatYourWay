package com.chat.yourway.service;

import com.chat.yourway.dto.request.ChangePassword;
import com.chat.yourway.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void changePassword(ChangePassword request, UserDetails userDetails) {
        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword()))
            throw new RuntimeException();

        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        contactRepository.changePasswordByUsername(newEncodedPassword, userDetails.getUsername());
    }
}
