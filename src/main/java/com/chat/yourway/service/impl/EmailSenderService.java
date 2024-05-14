package com.chat.yourway.service.impl;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
@RequiredArgsConstructor
public class EmailSenderService {

  @Value("${spring.mail.username}")
  private String emailAddressFrom;
  private final JavaMailSender javaMailSender;

  @Async
  public void sendEmail(EmailMessageDto request) {
    if (request == null) {
      throw new EmailSendingException("EmailSend request cannot be null");
    }

    MimeMessage mail = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mail);

    try {
      helper.setFrom(emailAddressFrom);
      helper.setTo(request.to());
      helper.setSubject(request.subject());
      helper.setText(request.text());
      javaMailSender.send(mail);
    } catch (MessagingException e) {
      throw new EmailSendingException("Error sending email");
    }
  }
}
