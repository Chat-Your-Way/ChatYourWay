package com.chat.yourway.service;

import com.chat.yourway.dto.request.ReceivedMessageDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.service.interfaces.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final MessageMapper messageMapper;
  private final String specificPrefix;

  public ChatMessageServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
      MessageMapper messageMapper, @Value("${socket.specific-prefix}") String specificPrefix) {
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.messageMapper = messageMapper;
    this.specificPrefix = specificPrefix;
  }

  @Override
  public MessageResponseDto sendToTopic(ReceivedMessageDto receivedMessageDto, String username) {

    receivedMessageDto.setSentFrom(username);
    receivedMessageDto.setSendTo("topic");

    MessageResponseDto messageResponseDto = messageMapper.toSendMessage(receivedMessageDto);

    messageResponseDto.setSentTime(LocalDateTime.now());
    log.info("{} sent message to topic", username);
    return messageResponseDto;
  }

  @Override
  public MessageResponseDto sendToUser(ReceivedMessageDto receivedMessageDto, String username) {

    String sendTo = receivedMessageDto.getSendTo();
    receivedMessageDto.setSentFrom(username);

    MessageResponseDto messageResponseDto = messageMapper.toSendMessage(receivedMessageDto);
    messageResponseDto.setSentTime(LocalDateTime.now());

    simpMessagingTemplate.convertAndSendToUser(sendTo, specificPrefix, messageResponseDto);
    log.info("{} sent message to {}", username, sendTo);
    return messageResponseDto;
  }

}
