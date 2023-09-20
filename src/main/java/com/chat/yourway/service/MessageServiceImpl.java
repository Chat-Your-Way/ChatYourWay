package com.chat.yourway.service;

import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.exception.MessageNotFoundException;
import com.chat.yourway.repository.MessageRepository;
import com.chat.yourway.service.interfaces.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
  private static final Byte MAX_AMOUNT_REPORTS = 2;

  private final MessageRepository messageRepository;

  @Override
  @Transactional
  public void reportMessageById(Integer messageId, UserDetails userDetails) {
    String email = userDetails.getUsername();

    if (!messageRepository.existsById(messageId)) {
      throw new MessageNotFoundException();
    } else if (messageRepository.hasReportByContactEmailAndMessageId(email, messageId)) {
      throw new MessageHasAlreadyReportedException();
    } else if (messageRepository.getCountReportsByMessageId(messageId) >= MAX_AMOUNT_REPORTS) {
      messageRepository.deleteById(messageId);
    } else {
      messageRepository.saveReportFromContactToMessage(email, messageId);
    }
  }
}
