package com.chat.yourway.service.impl;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.service.ChatMessageService;
import com.chat.yourway.service.ChatNotificationService;
import com.chat.yourway.service.ContactEventService;
import com.chat.yourway.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    private void sendToTopic(UUID topicId, MessageResponseDto messageDto) {
        var lastMessageDto = new LastMessageResponseDto();
        lastMessageDto.setTimestamp(messageDto.getTimestamp());
        //lastMessageDto.setSentFrom(messageDto.getSentFrom());
        lastMessageDto.setLastMessage(messageDto.getContent());

        contactEventService.updateMessageInfoForAllTopicSubscribers(topicId, lastMessageDto);

        simpMessagingTemplate.convertAndSend(toTopicDestination(topicId), messageDto);

        chatNotificationService.notifyTopicSubscribers(topicId);
        chatNotificationService.updateNotificationForAllWhoSubscribedToTopic(topicId);
    }

    private String toTopicDestination(UUID topicId) {
        return properties.getTopicPrefix() + "/" + topicId;
    }
}
