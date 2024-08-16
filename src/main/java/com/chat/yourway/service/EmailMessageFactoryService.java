package com.chat.yourway.service;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.model.email.EmailMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class EmailMessageFactoryService {

  public static final String TOKEN_PARAMETER = "?token=";

  public EmailMessageDto generateEmailMessage(EmailMessageInfoDto emailMessageInfoDto) {
    String path = emailMessageInfoDto.path();
    UUID uuidToken = emailMessageInfoDto.uuidToken();
    EmailMessageType emailMessageType = emailMessageInfoDto.emailMessageType();
    String link = generateLink(path, uuidToken, emailMessageType);
    log.info("Generated link: {}", link);
    String messageBody = String.format(emailMessageType.getMessageBody(), emailMessageInfoDto.username(), link);

    return new EmailMessageDto(emailMessageInfoDto.email(), emailMessageType.getSubject(), messageBody);
  }

  private String generateLink(String path, UUID uuidToken, EmailMessageType emailMessageType) {
    return path + emailMessageType.getEmailType() + TOKEN_PARAMETER + uuidToken;
  }
}
