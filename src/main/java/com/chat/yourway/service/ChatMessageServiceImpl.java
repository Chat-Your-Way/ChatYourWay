package com.chat.yourway.service;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.interfaces.ChatMessageService;
import com.chat.yourway.service.interfaces.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final MessageService messageService;

  @Value("${socket.dest-prefixes}")
  private String[] destPrefixes;

  @Transactional
  @Override
  public MessageResponseDto sendToTopic(Integer topicId, MessagePublicRequestDto message, String email) {
    log.trace("Started contact email: {} sendToTopic id: {}", email, topicId);
    MessageResponseDto messageResponseDto = messageService.createPublic(topicId, message, email);

    simpMessagingTemplate.convertAndSend(toTopicDestination(topicId), messageResponseDto);

    log.trace("{} sent message to topic id: {}", email, topicId);
    return messageResponseDto;
  }

  @Transactional
  @Override
  public MessageResponseDto sendToContact(MessagePrivateRequestDto message, String email) {
    String sendTo = message.getSendTo();
    log.trace("Started contact email: {} sendToContact email: {}", email, sendTo);
    MessageResponseDto messageResponseDto = messageService.createPrivate(message, email);

    simpMessagingTemplate.convertAndSendToUser(sendTo, destPrefixes[1], messageResponseDto);

    log.trace("{} sent message to {}", email, sendTo);
    return messageResponseDto;
  }

  private String toTopicDestination(Integer topicId) {
    return destPrefixes[0] + "/" + topicId;
  }

}
