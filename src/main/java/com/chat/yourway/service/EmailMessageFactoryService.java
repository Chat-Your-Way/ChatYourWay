package com.chat.yourway.service;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.model.enums.EmailMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.chat.yourway.utils.Constants.TOKEN_PARAMETER;

@Service
@Slf4j
public class EmailMessageFactoryService {
  public EmailMessageDto generateEmailMessage(EmailMessageInfoDto emailMessageInfoDto) {
    final var path = emailMessageInfoDto.path();
    final var uuidToken = emailMessageInfoDto.uuidToken();
    final var emailMessageType = emailMessageInfoDto.emailMessageType();
    final var link = generateLink(path, uuidToken, emailMessageType);

    log.info("Generated link: {}", link);

    final var messageBody = String.format(
            emailMessageType.getMessageBody(), emailMessageInfoDto.username(), link
    );

    return new EmailMessageDto(emailMessageInfoDto.email(), emailMessageType.getSubject(), messageBody);
  }

  private String generateLink(String path, UUID uuidToken, EmailMessageType emailMessageType) {
    return path + emailMessageType.getEmailType() + TOKEN_PARAMETER + uuidToken;
  }
}
