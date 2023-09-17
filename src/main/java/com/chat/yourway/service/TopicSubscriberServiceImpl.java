package com.chat.yourway.service;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.ContactMapper;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicSubscriberServiceImpl implements TopicSubscriberService {

  private final TopicSubscriberRepository topicSubscriberRepository;
  private final ContactMapper contactMapper;

  @Transactional
  @Override
  public void subscribeToTopicById(String email, Integer id) {
    log.trace("Started subscribeToTopic, contact email: {}, id: {}", email, id);

    if (hasContactSubscribedToTopic(email, id)) {
      log.warn("Contact email: {} already subscribed to the topic id: {}", email, id);
      throw new ContactAlreadySubscribedToTopicException(
          String.format("Contact email: %s already subscribed to the topic id: %s", email, id));
    }

    topicSubscriberRepository.subscribe(email, id);

    log.trace("Contact email: {} was subscribed to the Topic id: {}", email, id);
  }

  @Transactional
  @Override
  public void unsubscribeFromTopicById(String email, Integer id) {
    log.trace("Started unsubscribeFromTopic, contact email: {}, id: {}", email, id);

    if (!hasContactSubscribedToTopic(email, id)) {
      log.warn("Contact email: {} wasn't subscribed to the topic id: {}", email, id);
      throw new TopicSubscriberNotFoundException(
          String.format("Contact email: %s wasn't subscribed to the topic id: %s", email, id));
    }

    topicSubscriberRepository.unsubscribe(email, id);
    log.trace("Contact email: {} was unsubscribed from the Topic id: {}", email, id);
  }

  @Override
  public List<ContactResponseDto> findAllSubscribersByTopicId(Integer id) {
    log.trace("Started findAllSubscribersByTopicId id: {}", id);
    return topicSubscriberRepository.findAllActiveSubscribersByTopicId(id).stream()
        .map(contactMapper::toResponseDto)
        .toList();
  }

  @Override
  public boolean hasContactSubscribedToTopic(String email, Integer topicId) {
    log.trace("Checking if contact {} has subscribed to topic {}", email, topicId);
    return topicSubscriberRepository
        .existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(email, topicId);
  }

}
