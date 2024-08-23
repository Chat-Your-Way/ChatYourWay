package com.chat.yourway.service;

import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.dto.request.RestorePasswordDto;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.email.EmailMessageType;
import com.chat.yourway.model.email.EmailToken;
import com.chat.yourway.repository.jpa.EmailTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordService {

    private final PasswordEncoder passwordEncoder;
    private final ContactService contactService;
    private final EmailTokenRepository emailTokenRepository;
    private final EmailMessageFactoryService emailMessageFactoryService;
    private final EmailSenderService emailSenderService;

    @Transactional
    public void changePassword(ChangePasswordDto request) {
        Contact contact = contactService.getCurrentContact();
        contactService.verifyPassword(request.getOldPassword(), contact.getPassword());
        contactService.changePasswordByEmail(request.getNewPassword(), contact.getEmail());
    }

    @Transactional
    public void sendEmailToRestorePassword(String email, String clientHost) {
        var contact = contactService.findByEmail(email);
        var emailToken = EmailToken.builder()
                        .messageType(EmailMessageType.RESTORE_PASSWORD)
                        .contact(contact)
                        .build();

        emailTokenRepository.save(emailToken);

        var emailMessageInfo = new EmailMessageInfoDto(contact.getNickname(),
                contact.getEmail(),
                emailToken.getToken(),
                clientHost,
                EmailMessageType.RESTORE_PASSWORD);
        var emailMessage = emailMessageFactoryService.generateEmailMessage(emailMessageInfo);

        emailSenderService.sendEmail(emailMessage);
    }

    @Transactional
    public void restorePassword(RestorePasswordDto restorePasswordDto) {
        var emailToken = emailTokenRepository.findById(restorePasswordDto.getEmailToken())
                .orElseThrow(EmailTokenNotFoundException::new);
        var contact = emailToken.getContact();
        var newEncodedPassword = passwordEncoder.encode(restorePasswordDto.getNewPassword());

        contact.setPassword(newEncodedPassword);
        emailTokenRepository.delete(emailToken);
    }

}
