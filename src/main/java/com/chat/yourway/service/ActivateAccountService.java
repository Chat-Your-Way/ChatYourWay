package com.chat.yourway.service;

import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.model.Contact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chat.yourway.model.enums.EmailMessageType.ACTIVATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateAccountService {

    private final EmailSenderService emailSenderService;
    private final EmailMessageFactoryService emailMessageFactoryService;
    private final ContactService contactService;

    @Transactional
    public void activateAccount() {
        log.trace("Started activateAccount by email");

        final var contact = contactService.getCurrentContact();
        contact.setActive(true);
        contactService.save(contact);

        log.info("Account is activate for contact email [{}]", contact.getEmail());
    }

    public void sendVerifyEmail(Contact contact, String clientHost) {
        log.trace("Started sendVerifyEmail by contact email [{}], and client host [{}]", contact.getEmail(), clientHost);

        final var emailMessageInfoDto = EmailMessageInfoDto.builder()
                .username(contact.getNickname())
                .email(contact.getEmail())
                .path(clientHost)
                .emailMessageType(ACTIVATE)
                .build();

        final var emailMessage = emailMessageFactoryService.generateEmailMessage(emailMessageInfoDto);

        emailSenderService.sendEmail(emailMessage);

        log.info("Verifying account email was sent to contact email [{}]", contact.getEmail());
    }
}
