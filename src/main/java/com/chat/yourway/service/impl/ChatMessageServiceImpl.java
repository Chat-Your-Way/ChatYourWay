package com.chat.yourway.service.impl;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.ChatMessageService;
import com.chat.yourway.service.ChatNotificationService;
import com.chat.yourway.service.ContactEventService;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.MessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

  private final WebsocketProperties properties;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final MessageService messageService;
  private final ContactEventService contactEventService;
  private final ChatNotificationService chatNotificationService;
  private final ContactService contactService;

  @Transactional
  @Override
  public MessageResponseDto sendToPublicTopic(Integer topicId, MessagePublicRequestDto message,
      String email) {
    log.trace("Started contact email: [{}] sendToTopic id: [{}]", email, topicId);
    MessageResponseDto messageResponseDto = messageService.createPublic(topicId, message, email);
    messageResponseDto.setSentFrom(contactService.findByEmail(email).getNickname());

    sendToTopic(topicId, messageResponseDto);

    log.trace("Contact [{}] sent message to topic id: [{}]", email, topicId);
    return messageResponseDto;
  }

  @Transactional
  @Override
  public MessageResponseDto sendToPrivateTopic(Integer topicId, MessagePrivateRequestDto message,
      String email) {
    String sendTo = message.getSendTo();
    log.trace("Started contact email: [{}] sendToPrivateTopic email: [{}]", email, sendTo);
    MessageResponseDto messageResponseDto = messageService.createPrivate(message, email);

    messageResponseDto.setSentFrom(contactService.findByEmail(email).getNickname());
    messageResponseDto.setSendTo(
        contactService.findByEmail(messageResponseDto.getSendTo()).getNickname());

    sendToTopic(topicId, messageResponseDto);

    log.trace("Contact [{}] sent message to [{}]", email, sendTo);
    return messageResponseDto;
  }

  @Override
  public List<MessageResponseDto> sendMessageHistoryByTopicId(Integer topicId,
      PageRequestDto pageRequestDto, String email) {
    log.trace("Started sendMessageHistoryByTopicId = [{}]", topicId);

    List<MessageResponseDto> messages = messageService.findAllByTopicId(topicId, pageRequestDto)
        .stream()
        .peek(m -> {
          m.setSentFrom(contactService.findByEmail(m.getSentFrom()).getNickname());
          if (isPrivate(m)) {
            m.setSendTo(contactService.findByEmail(m.getSendTo()).getNickname());
          }
        })
        .toList();
    simpMessagingTemplate.convertAndSendToUser(email, toTopicDestination(topicId), messages);

    log.trace("Message history was sent to topicId = [{}]", topicId);
    return messages;
  }

  private void sendToTopic(Integer topicId, MessageResponseDto messageDto) {
    var lastMessageDto = new LastMessageResponseDto();
    lastMessageDto.setTimestamp(messageDto.getTimestamp());
    lastMessageDto.setSentFrom(messageDto.getSentFrom());
    lastMessageDto.setLastMessage(messageDto.getContent());

    contactEventService.updateMessageInfoForAllTopicSubscribers(topicId, lastMessageDto);

    simpMessagingTemplate.convertAndSend(toTopicDestination(topicId), messageDto);

    chatNotificationService.notifyTopicSubscribers(topicId);
    chatNotificationService.updateNotificationForAllWhoSubscribedToTopic(topicId);
  }

  private String toTopicDestination(Integer topicId) {
    return properties.getTopicPrefix() + "/" + topicId;
  }

  private boolean isPrivate(MessageResponseDto message) {
    return message.getSendTo().contains("@");
  }

}
