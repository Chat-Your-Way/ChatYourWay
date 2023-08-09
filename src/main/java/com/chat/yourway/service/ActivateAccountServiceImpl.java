package com.chat.yourway.service;

import static com.chat.yourway.model.email.EmailMessageConstant.TOKEN_PARAMETER;
import static com.chat.yourway.model.email.EmailMessageConstant.VERIFY_ACCOUNT_SUBJECT;
import static com.chat.yourway.model.email.EmailMessageConstant.VERIFY_ACCOUNT_TEXT;
import static com.chat.yourway.model.email.EmailMessageType.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.chat.yourway.exception.ServiceException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.email.EmailMessageType;
import com.chat.yourway.model.email.EmailSend;
import com.chat.yourway.model.email.EmailToken;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateAccountServiceImpl implements ActivateAccountService {

  private final EmailSenderServiceImpl emailSenderServiceImpl;
  private final EmailTokenRepository emailTokenRepository;

  @Transactional
  @Override
  public void activateAccount(String token) {
    EmailToken emailToken = emailTokenRepository.findById(token)
        .orElseThrow(() -> new ServiceException(NOT_FOUND,
            String.format("Email token: %s wasn't found in repository", token)));

    Contact contact = emailToken.getContact();

    contact.setIsActive(true);
    emailTokenRepository.delete(emailToken);
  }

  @Override
  public void sendVerifyEmail(Contact contact, HttpServletRequest httpRequest) {
    String uuid = generateUUID();
    String link = generateLink(httpRequest, uuid, ACTIVATE);
    saveEmailToken(contact, uuid);

    String text = String.format(VERIFY_ACCOUNT_TEXT, contact.getUsername(), link);
    EmailSend emailSend = new EmailSend(contact.getEmail(), VERIFY_ACCOUNT_SUBJECT, text);

    emailSenderServiceImpl.sendEmail(emailSend);
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

  private String generateLink(HttpServletRequest httpRequest, String uuid,
      EmailMessageType emailMessageType) {
    log.info("Generate link for verifying account");
    return httpRequest.getHeader(HttpHeaders.REFERER) +
        emailMessageType.getEmailType() +
        TOKEN_PARAMETER +
        uuid;
  }

}
