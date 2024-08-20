package com.chat.yourway.service;

import com.chat.yourway.dto.request.MessageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.exception.MessageNotFoundException;
import com.chat.yourway.exception.MessagePermissionDeniedException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.Topic;
import com.chat.yourway.model.TopicScope;
import com.chat.yourway.repository.jpa.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
    public MessageResponseDto sendToTopic(UUID topicId, MessageRequestDto message) {
        Topic topic = topicService.getTopic(topicId);
        Contact contact = contactService.getCurrentContact();
        log.trace("Creating public message in topic ID: {} by contact email: {}", topicId, contact.getEmail());

        validateSubscription(topic, contact);

        Message savedMessage = messageRepository.save(
                new Message(topic, contact, message.getContent())
        );

        contactService.addUnreadMessageToTopicSubscribers(contact, savedMessage);
        notificationService.sendPublicMessage(contactOnlineService.getOnlineContacts(), savedMessage);

        log.trace("Public message from email: {} to topic id: {} was created", contact.getEmail(), topicId);
        return messageMapper.toResponseDto(savedMessage, contact);
    }

    @Transactional
    public MessageResponseDto sendToContact(String sendToEmail, MessageRequestDto message) {
        Contact sendToContact = contactService.findByEmail(sendToEmail);
        if (!sendToContact.isPermittedSendingPrivateMessage()) {
            throw new MessagePermissionDeniedException(
                    String.format("You cannot send private messages to a contact from an sendFromEmail: %s",
                            sendToEmail));
        }
        Contact sendFromContact = contactService.getCurrentContact();

        Topic topic = topicService.getPrivateTopic(sendToContact, sendFromContact);
        Message savedMessage = messageRepository.save(
                new Message(topic, sendFromContact, message.getContent())
        );

        contactService.addUnreadMessageToTopicSubscribers(sendFromContact, savedMessage);
        notificationService.sendPrivateMessage(savedMessage);
        log.trace("Private message from sendFromEmail: {} to sendFromEmail id: {} was created",
                sendFromContact.getEmail(), sendToEmail);
        return messageMapper.toResponseDto(savedMessage, sendFromContact);
    }

    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow(MessageNotFoundException::new);
    }

    @Transactional
    public void reportMessageById(UUID messageId) {
        Contact contact = contactService.getCurrentContact();
        log.trace("Contact email: {} is reporting message with ID: {}", contact.getEmail(), messageId);

        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException();
        } else if (messageRepository.getCountReportsByMessageId(messageId) >= maxAmountReports) {
            messageRepository.deleteById(messageId);
        } else {
            messageRepository.saveReportFromContactToMessage(contact.getEmail(), messageId);
        }
    }

    public Page<MessageResponseDto> findAllByTopicId(UUID topicId, Pageable pageable) {
        Topic topic = topicService.getTopic(topicId);
        Contact contact = contactService.getCurrentContact();

        if (TopicScope.PRIVATE.equals(topic.getScope())) {
            validateSubscription(topic, contact);
        }

        Page<Message> messages = messageRepository.findAllByTopicId(topicId, pageable);
        return messages.map(message -> messageMapper.toResponseDto(message, contact));
    }

    @Transactional
    public void readMessage(UUID messageId) {
        Contact contact = contactService.getCurrentContact();
        Message message = findById(messageId);

        List<Contact> onlineContacts = contactOnlineService.getOnlineContacts();
        notificationService.topicChange(onlineContacts, message.getTopic());

        contactService.deleteUnreadMessage(contact, message);
    }

    private void validateSubscription(Topic topic, Contact contact) {
        log.trace("Validating subscription of contact email: {} to topic ID: {}", contact.getEmail(), topic.getId());
        if (!topicSubscriberService.hasContactSubscribedToTopic(topic, contact)) {
            log.warn("Contact email: {} wasn't subscribed to the topic id: {}", contact.getEmail(), topic.getId());
            throw new TopicSubscriberNotFoundException(
                    String.format("Contact email: %s wasn't subscribed to the topic id: %s",
                            contact.getEmail(), topic.getId())
            );
        }
    }
}
