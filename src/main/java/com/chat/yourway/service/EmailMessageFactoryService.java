package com.chat.yourway.service;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.model.enums.EmailMessageType;
import com.chat.yourway.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.chat.yourway.utils.Constants.TOKEN_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageFactoryService {

  private final JwtService jwtService;
  private final ContactService contactService;

  public EmailMessageDto generateEmailMessage(EmailMessageInfoDto emailMessageInfoDto) {
    final var path = emailMessageInfoDto.path();
    final var emailMessageType = emailMessageInfoDto.emailMessageType();
    final var contact = contactService.findByEmail(emailMessageInfoDto.email());
    final var link = generateLink(path, jwtService.generateEmailToken(contact), emailMessageType);

    log.info("Generated link: {}", link);

    final var messageBody = String.format(
            emailMessageType.getMessageBody(), emailMessageInfoDto.username(), link
    );

    return new EmailMessageDto(emailMessageInfoDto.email(), emailMessageType.getSubject(), messageBody);
  }

  private String generateLink(String path, String token, EmailMessageType emailMessageType) {
    return path + emailMessageType.getEmailType() + TOKEN_PARAMETER + token;
  }
}
