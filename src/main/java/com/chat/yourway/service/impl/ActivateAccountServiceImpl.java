package com.chat.yourway.service.impl;

import static com.chat.yourway.model.email.EmailMessageType.ACTIVATE;

import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.email.EmailToken;
import com.chat.yourway.repository.jpa.ContactRepository;
import com.chat.yourway.repository.jpa.EmailTokenRepository;
import com.chat.yourway.service.ActivateAccountService;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.EmailMessageFactoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateAccountServiceImpl implements ActivateAccountService {

  private final EmailSenderService emailSenderService;
  private final EmailTokenRepository emailTokenRepository;
  private final EmailMessageFactoryService emailMessageFactoryService;
  private final ContactService contactService;

  @Transactional
  @Override
  public void activateAccount(UUID token) {
    log.trace("Started activateAccount by email token");

    EmailToken emailToken = emailTokenRepository.findById(token)
        .orElseThrow(() -> {
          log.warn("Current email token does not exist in repository");
          return new EmailTokenNotFoundException();
        });

    Contact contact = emailToken.getContact();
    contact.setActive(true);
    contactService.save(contact);

    emailTokenRepository.delete(emailToken);

    log.info("Account is activate for contact email [{}]", contact.getEmail());
  }

  @Override
  public void sendVerifyEmail(Contact contact, String clientHost) {
    log.trace("Started sendVerifyEmail by contact email [{}], and client host [{}]",
        contact.getEmail(), clientHost);

    EmailToken emailToken = saveEmailToken(contact);

    var emailMessageInfoDto = new EmailMessageInfoDto(contact.getNickname(), contact.getEmail(),
            emailToken.getToken(), clientHost, ACTIVATE);
    var emailMessage = emailMessageFactoryService.generateEmailMessage(emailMessageInfoDto);

    emailSenderService.sendEmail(emailMessage);

    log.info("Verifying account email was sent to contact email [{}]", contact.getEmail());
  }

  private EmailToken saveEmailToken(Contact contact) {
    EmailToken emailToken = EmailToken.builder()
        .contact(contact)
        .messageType(ACTIVATE)
        .build();

    emailTokenRepository.save(emailToken);
    return emailToken;
  }

}
