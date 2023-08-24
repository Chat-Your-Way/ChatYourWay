package com.chat.yourway.service.impl;

import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.model.EmailMessageType;
import com.chat.yourway.model.EmailToken;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.EmailMessageFactoryService;
import com.chat.yourway.service.EmailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.REFERER;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailMessageFactoryService emailMessageFactoryService;
    private final EmailSenderService emailSenderService;

    @Override
    @Transactional
    public void changePassword(ChangePasswordDto request, UserDetails userDetails) {
        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            throw new OldPasswordsIsNotEqualToNewException("Old password is not correct, try again.");
        }

        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        contactRepository.changePasswordByUsername(newEncodedPassword, userDetails.getUsername());
    }

    @Override
    public void sendEmailToRestorePassword(String email, String clientAddress) {
        var contact = contactRepository.findByEmail(email).orElseThrow(() ->
            new ContactNotFoundException(String.format("Contact with email (%s) does not exist", email)));
        var uuidToken = UUID.randomUUID().toString();
        var emailToken = EmailToken.builder().token(uuidToken)
                .messageType(EmailMessageType.RESTORE_PASSWORD)
                .contact(contact)
                .build();

        emailTokenRepository.save(emailToken);

        var emailMessageInfo = new EmailMessageInfoDto(contact.getUsername(),
                contact.getEmail(),
                uuidToken,
                clientAddress,
                EmailMessageType.RESTORE_PASSWORD);
        var emailMessage = emailMessageFactoryService.generateEmailMessage(emailMessageInfo);

        emailSenderService.sendEmail(emailMessage);
    }

    @Override
    @Transactional
    public void restorePassword(String newPassword, String token) {
        var emailToken = emailTokenRepository.findById(token)
                .orElseThrow(EmailTokenNotFoundException::new);
        var contact = emailToken.getContact();
        var newEncodedPassword = passwordEncoder.encode(newPassword);

        contact.setPassword(newEncodedPassword);
        emailTokenRepository.delete(emailToken);
    }
}
