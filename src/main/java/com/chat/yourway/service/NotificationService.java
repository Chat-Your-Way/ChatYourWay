package com.chat.yourway.service;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.notification.ContactResponseDto;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.TopicScope;
import com.chat.yourway.model.redis.ContactOnline;
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
    private final MessageMapper messageMapper;

    public void sendPublicMessage(List<Contact> sendToContacts, Message message) {
        if (TopicScope.PUBLIC.equals(message.getTopic().getScope())) {
            String notifyMessageDestination = toNotifyMessageDestination();
            for (Contact onlineUser : sendToContacts) {
                MessageResponseDto responseDto = messageMapper.toResponseDto(message, onlineUser);
                simpMessagingTemplate.convertAndSendToUser(
                        onlineUser.getEmail(), notifyMessageDestination, responseDto
                );
            }
        }
    }

    public void sendPrivateMessage(Message message) {
        if (TopicScope.PRIVATE.equals(message.getTopic().getScope())) {
            String notifyMessageDestination = toNotifyMessageDestination();
            List<Contact> topicSubscribers = message.getTopic().getTopicSubscribers();
            for (Contact onlineUser : topicSubscribers) {
                MessageResponseDto responseDto = messageMapper.toResponseDto(message, onlineUser);
                simpMessagingTemplate.convertAndSendToUser(
                        onlineUser.getEmail(), notifyMessageDestination, responseDto
                );
            }
        }
    }

    public void contactChangeStatus(List<Contact> sendToContacts, Contact contact) {
        contactChangeStatus(sendToContacts, contact, null);
    }

    public void contactChangeStatus(List<Contact> sendToContacts, Contact contact, ContactOnline contactOnline) {
        ContactResponseDto contactResponseDto = new ContactResponseDto(contact.getId());

        boolean online = false;
        if (contactOnline != null) {
            online = true;
            contactResponseDto.setCurrentTopicId(contactOnline.getTopicId());
            contactResponseDto.setTypingStatus(contactOnline.isTypingStatus());
        }
        contactResponseDto.setOnline(online);

        String notifyContactsDestination = toNotifyContactsDestination();
        for (Contact onlineUser : sendToContacts) {
            simpMessagingTemplate.convertAndSendToUser(
                    onlineUser.getEmail(), notifyContactsDestination, contactResponseDto
            );
        }
    }

    private String toNotifyMessageDestination() {
        return properties.getNotifyPrefix() + "/messages";
    }

    private String toNotifyTopicsDestination() {
        return properties.getNotifyPrefix() + "/topics";
    }

    private String toNotifyContactsDestination() {
        return properties.getNotifyPrefix() + "/contacts";
    }
}
