package com.chat.yourway.service.impl;

import com.chat.yourway.dto.request.MessageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.exception.MessageNotFoundException;
import com.chat.yourway.exception.MessagePermissionDeniedException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.Topic;
import com.chat.yourway.model.TopicScope;
import com.chat.yourway.repository.jpa.MessageRepository;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.TopicService;
import com.chat.yourway.service.TopicSubscriberService;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final TopicService topicService;
    private final TopicSubscriberService topicSubscriberService;
    private final ContactService contactService;
    private final NotificationService notificationService;
    private final ContactOnlineService contactOnlineService;

    @Value("${message.max.amount.reports}")
    private Byte maxAmountReports;

    @Transactional
    public MessageResponseDto sendToTopic(UUID topicId, MessageRequestDto message, String email) {
        log.trace("Creating public message in topic ID: {} by contact email: {}", topicId, email);
        Topic topic = topicService.getTopic(topicId);
        Contact contact = contactService.findByEmail(email);

        validateSubscription(topic, contact);

        Message savedMessage = messageRepository.save(
            new Message(topic, contact, message.getContent())
        );
        notificationService.sendPublicMessage(contactOnlineService.getOnlineContacts(), savedMessage);

        log.trace("Public message from email: {} to topic id: {} was created", email, topicId);
        return messageMapper.toResponseDto(savedMessage, contact);
    }

    @Transactional
    public MessageResponseDto sendToContact(String sendToEmail, MessageRequestDto message,
        String sendFromEmail) {
        Contact sendToContact = contactService.findByEmail(sendToEmail);
        if (!sendToContact.isPermittedSendingPrivateMessage()) {
            throw new MessagePermissionDeniedException(
                String.format("You cannot send private messages to a contact from an sendFromEmail: %s",
                    sendToEmail));
        }
        Contact sendFromContact = contactService.findByEmail(sendFromEmail);

        Topic topic = topicService.getPrivateTopic(sendToContact, sendFromContact);
        Message savedMessage = messageRepository.save(
            new Message(topic, sendFromContact, message.getContent())
        );

        notificationService.sendPrivateMessage(savedMessage);
        log.trace("Private message from sendFromEmail: {} to sendFromEmail id: {} was created",
            sendFromEmail, sendToEmail);
        return messageMapper.toResponseDto(savedMessage, sendFromContact);
    }

    @Transactional
    public void reportMessageById(Integer messageId, String email) {
        log.trace("Contact email: {} is reporting message with ID: {}", email, messageId);

        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException();
//    } else if (messageRepository.hasReportByContactEmailAndMessageId(email, messageId)) {
//      throw new MessageHasAlreadyReportedException();
        } else if (messageRepository.getCountReportsByMessageId(messageId) >= maxAmountReports) {
            messageRepository.deleteById(messageId);
        } else {
            messageRepository.saveReportFromContactToMessage(email, messageId);
        }
    }

    public int countMessagesBetweenTimestampByTopicId(UUID topicId, String sentFrom,
        LocalDateTime timestamp) {
        log.trace("Started countMessagesBetweenTimestampByTopicId [{}]", topicId);

        return messageRepository.countMessagesBetweenTimestampByTopicId(topicId, sentFrom,
            timestamp,
            LocalDateTime.now());
    }

    public List<LastMessageResponseDto> getLastMessages(List<UUID> topicIds, TopicScope scope) {
        if (topicIds == null) {
            return messageRepository.getLastMessages(scope);
        } else {
            return messageRepository.getLastMessagesByTopicIds(scope, topicIds);
        }
    }

    public Page<MessageResponseDto> findAllByTopicId(UUID topicId, Pageable pageable,
        Principal principal) {
        Topic topic = topicService.getTopic(topicId);
        Contact contact = contactService.findByEmail(principal.getName());

        if (TopicScope.PRIVATE.equals(topic.getScope())) {
            validateSubscription(topic, contact);
        }

        Page<Message> messages = messageRepository.findAllByTopicId(topicId, pageable);
        return messages.map(m -> messageMapper.toResponseDto(m, contact));
    }

    private void validateSubscription(Topic topic, Contact contact) {
        log.trace("Validating subscription of contact email: {} to topic ID: {}",
            contact.getEmail(), topic.getId());
        if (!topicSubscriberService.hasContactSubscribedToTopic(topic, contact)) {
            log.warn("Contact email: {} wasn't subscribed to the topic id: {}", contact.getEmail(),
                topic.getId());
            throw new TopicSubscriberNotFoundException(
                String.format("Contact email: %s wasn't subscribed to the topic id: %s",
                    contact.getEmail(), topic.getId()));
        }
    }
}
