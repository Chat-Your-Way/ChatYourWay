package com.chat.yourway.service;

import static com.chat.yourway.model.email.EmailMessageType.ACTIVATE;

import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.email.EmailToken;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.chat.yourway.service.interfaces.EmailMessageFactoryService;
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

  @Transactional
  @Override
  public void activateAccount(String token) {
    EmailToken emailToken = emailTokenRepository.findById(token)
        .orElseThrow(EmailTokenNotFoundException::new);

    Contact contact = emailToken.getContact();

    contact.setIsActive(true);
    emailTokenRepository.delete(emailToken);
  }

  @Override
  public void sendVerifyEmail(Contact contact, String clientHost) {
    String uuid = generateUUID();
    saveEmailToken(contact, uuid);

    var emailMessageInfoDto = new EmailMessageInfoDto(contact.getNickname(), contact.getEmail(),
        uuid, clientHost, ACTIVATE);
    var emailMessage = emailMessageFactoryService.generateEmailMessage(emailMessageInfoDto);

    emailSenderService.sendEmail(emailMessage);
    log.info("Email for verifying account sent");
  }

  private void saveEmailToken(Contact contact, String uuid) {
    EmailToken emailToken = EmailToken.builder()
        .contact(contact)
        .token(uuid)
        .messageType(ACTIVATE)
        .build();

    emailTokenRepository.save(emailToken);
  }

  private String generateUUID() {
    return UUID.randomUUID().toString();
  }

}
