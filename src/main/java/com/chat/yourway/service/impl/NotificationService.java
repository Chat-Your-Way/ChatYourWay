package com.chat.yourway.service.impl;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.TopicScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final WebsocketProperties properties;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ContactOnlineService contactOnlineService;
    private final MessageMapper messageMapper;

    public void sendPublicMessage(Message message) {
        if (TopicScope.PUBLIC.equals(message.getTopic().getScope())) {
            List<Contact> onlineUsers = contactOnlineService.getOnlineContacts();
            for (Contact onlineUser : onlineUsers) {
                MessageResponseDto responseDto = messageMapper.toResponseDto(message, onlineUser);
                simpMessagingTemplate.convertAndSendToUser(
                        onlineUser.getEmail(), toNotifyMessageDestination(), responseDto
                );
            }
        }
    }

    public void sendPrivateMessage(Message message) {
        if (TopicScope.PRIVATE.equals(message.getTopic().getScope())) {
            List<Contact> topicSubscribers = message.getTopic().getTopicSubscribers();
            for (Contact onlineUser : topicSubscribers) {
                MessageResponseDto responseDto = messageMapper.toResponseDto(message, onlineUser);
                simpMessagingTemplate.convertAndSendToUser(
                        onlineUser.getEmail(), toNotifyMessageDestination(), responseDto
                );
            }
        }
    }

    private String toNotifyMessageDestination() {
        return properties.getNotifyPrefix() + "/messages";
    }

    private String toNotifyTopicsDestination() {
        return properties.getNotifyPrefix() + "/topics";
    }
}
