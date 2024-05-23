package com.chat.yourway.service.impl;

import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.MessageNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.mapper.TopicMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.jpa.MessageRepository;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.MessageService;
import com.chat.yourway.service.TopicService;
import com.chat.yourway.service.TopicSubscriberService;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

  @Value("${message.max.amount.reports}")
  private Byte maxAmountReports;

  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final TopicService topicService;
  private final TopicMapper topicMapper;
  private final TopicSubscriberService topicSubscriberService;
  private final ContactService contactService;

  @Transactional
  @Override
  public MessageResponseDto sendToTopic(UUID topicId, MessageRequestDto message,
                                         String email) {
    log.trace("Creating public message in topic ID: {} by contact email: {}", topicId, email);
    Topic topic = topicService.getTopic(topicId);
    Contact contact = contactService.findByEmail(email);

    validateSubscription(topic, contact);

    Message savedMessage = messageRepository.save(Message.builder()
        .sender(contact)
        .content(message.getContent())
        .timestamp(LocalDateTime.now())
        .topic(topic)
        .build());

    log.trace("Public message from email: {} to topic id: {} was created", email, topicId);
    return messageMapper.toResponseDto(savedMessage);
  }

  @Transactional
  @Override
  public MessageResponseDto createPrivate(MessagePrivateRequestDto message, String email) {
    String sendTo = message.getSendTo();

    log.trace("Creating private message from contact email: {} to recipient: {}", email, sendTo);
    String privateName = topicService.generatePrivateName(sendTo, email);
    TopicResponseDto topic = topicService.findByName(privateName);

//    if (topicSubscriberService.hasProhibitionSendingPrivateMessages(topic.getId())) {
//      throw new MessagePermissionDeniedException(
//          "You do not have permission for sending message to private topic");
//    }

    Message savedMessage = messageRepository.save(Message.builder()
        //.sentFrom(email)
        //.sendTo(message.getSendTo())
        .content(message.getContent())
        .timestamp(LocalDateTime.now())
        .topic(topicMapper.toEntity(topic))
        .build());

    log.trace("Private message from email: {} to email: {} was created", email, sendTo);
    return messageMapper.toResponseDto(savedMessage);
  }

  @Override
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

  @Override
  public int countMessagesBetweenTimestampByTopicId(UUID topicId, String sentFrom,
      LocalDateTime timestamp) {
    log.trace("Started countMessagesBetweenTimestampByTopicId [{}]", topicId);

    return messageRepository.countMessagesBetweenTimestampByTopicId(topicId, sentFrom, timestamp,
        LocalDateTime.now());
  }

  @Override
  public Page<MessageResponseDto> findAllByTopicId(UUID topicId, Pageable pageable,
      Principal principal) {
    Topic topic = topicService.getTopic(topicId);
    Contact contact = contactService.findByEmail(principal.getName());

    validateSubscription(topic, contact);

    Page<Message> messages = messageRepository.findAllByTopicId(topicId, pageable);
    return messages.map(m -> messageMapper.toResponseDto(m));
  }

  private void validateSubscription(Topic topic, Contact contact) {
    log.trace("Validating subscription of contact email: {} to topic ID: {}", contact.getEmail(), topic.getId());
    if (!topicSubscriberService.hasContactSubscribedToTopic(topic, contact)) {
      log.warn("Contact email: {} wasn't subscribed to the topic id: {}", contact.getEmail(), topic.getId());
      throw new TopicSubscriberNotFoundException(
          String.format("Contact email: %s wasn't subscribed to the topic id: %s", contact.getEmail(), topic.getId()));
    }
  }

}
