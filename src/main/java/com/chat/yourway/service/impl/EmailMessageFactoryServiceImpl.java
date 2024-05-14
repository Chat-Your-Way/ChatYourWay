package com.chat.yourway.service.impl;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.model.email.EmailMessageType;
import com.chat.yourway.service.EmailMessageFactoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailMessageFactoryServiceImpl implements EmailMessageFactoryService {

  public static final String TOKEN_PARAMETER = "?token=";

  @Override
  public EmailMessageDto generateEmailMessage(EmailMessageInfoDto emailMessageInfoDto) {
    String path = emailMessageInfoDto.path();
    String uuidToken = emailMessageInfoDto.uuidToken();
    EmailMessageType emailMessageType = emailMessageInfoDto.emailMessageType();
    String link = generateLink(path, uuidToken, emailMessageType);
    log.info("Generated link: {}", link);
    String messageBody = String.format(emailMessageType.getMessageBody(),
        emailMessageInfoDto.username(), link);

    return new EmailMessageDto(emailMessageInfoDto.email(), emailMessageType.getSubject(),
        messageBody);
  }

  private String generateLink(String path, String uuidToken, EmailMessageType emailMessageType) {
    return path +
        emailMessageType.getEmailType() +
        TOKEN_PARAMETER +
        uuidToken;
  }
}
