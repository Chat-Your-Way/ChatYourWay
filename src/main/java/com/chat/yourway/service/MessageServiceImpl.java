package com.chat.yourway.service;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.exception.MessageNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.MessageMapper;
import com.chat.yourway.mapper.TopicMapper;
import com.chat.yourway.model.Message;
import com.chat.yourway.repository.MessageRepository;
import com.chat.yourway.service.interfaces.MessageService;
import com.chat.yourway.service.interfaces.TopicService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  @Transactional
  @Override
  public MessageResponseDto createPublic(int topicId, MessagePublicRequestDto message,
      String email) {
    log.trace("Creating public message in topic ID: {} by contact email: {}", topicId, email);
    TopicResponseDto topic = topicService.findById(topicId);

    validateSubscription(topicId, email);

    Message savedMessage = messageRepository.save(Message.builder()
        .sentFrom(email)
        .sendTo("Topic id=" + topic.getId())
        .content(message.getContent())
        .timestamp(LocalDateTime.now())
        .topic(topicMapper.toEntity(topic))
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

    Message savedMessage = messageRepository.save(Message.builder()
        .sentFrom(email)
        .sendTo(message.getSendTo())
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
    } else if (messageRepository.hasReportByContactEmailAndMessageId(email, messageId)) {
      throw new MessageHasAlreadyReportedException();
    } else if (messageRepository.getCountReportsByMessageId(messageId) >= maxAmountReports) {
      messageRepository.deleteById(messageId);
    } else {
      messageRepository.saveReportFromContactToMessage(email, messageId);
    }
  }

  @Override
  public List<MessageResponseDto> findAllByTopicId(Integer topicId) {
    log.trace("Get all messages for topic with ID: {}", topicId);
    List<Message> messages = messageRepository.findAllByTopicId(topicId);
    log.trace("Getting all messages for topic with ID: {}", topicId);
    return messageMapper.toListResponseDto(messages);
  }

  @Override
  public int countMessagesBetweenTimestampByTopicId(Integer topicId, String sentFrom,
      LocalDateTime timestamp) {
    log.trace("Started countMessagesBetweenTimestampByTopicId [{}]", topicId);

    return messageRepository.countMessagesBetweenTimestampByTopicId(topicId, sentFrom, timestamp,
        LocalDateTime.now());
  }

  private void validateSubscription(Integer topicId, String email) {
    log.trace("Validating subscription of contact email: {} to topic ID: {}", email, topicId);
    if (!topicSubscriberService.hasContactSubscribedToTopic(email, topicId)) {
      log.warn("Contact email: {} wasn't subscribed to the topic id: {}", email, topicId);
      throw new TopicSubscriberNotFoundException(
          String.format("Contact email: %s wasn't subscribed to the topic id: %s", email, topicId));
    }
  }

}
