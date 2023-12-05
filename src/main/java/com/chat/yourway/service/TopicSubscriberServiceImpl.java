package com.chat.yourway.service;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.NotSubscribedTopicException;
import com.chat.yourway.exception.OwnerCantUnsubscribedException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.ContactMapper;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicSubscriberServiceImpl implements TopicSubscriberService {

  private final TopicRepository topicRepository;
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
      log.warn("Contact email: {} wasn't unsubscribed from the topic id: {}", email, id);
      throw new TopicSubscriberNotFoundException(
          String.format("Contact email: %s wasn't unsubscribed from the topic id: %s", email, id));
    }

    if (isTopicCreator(id, email)) {
      log.warn("Topic owner: {} can't unsubscribed from the topic id: {}", email, id);
      throw new OwnerCantUnsubscribedException(
          String.format("Topic owner: %s can't unsubscribed from the topic id: %s", email, id));
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
    return topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(
        email, topicId);
  }

  @Override
  @Transactional
  public void addTopicToFavourite(Integer topicId, UserDetails userDetails) {
    String contactEmail = userDetails.getUsername();
    boolean isFavouriteTopic = true;

    if (!topicRepository.existsById(topicId)) {
      throw new TopicNotFoundException(String.format("Topic with id [%d] is not found.", topicId));
    } else if (!topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(
        contactEmail, topicId)) {
      throw new NotSubscribedTopicException(
          "You cannot mark topic as favourite because you did not subscribe before");
    }

    topicSubscriberRepository.updateFavouriteTopicStatusByTopicIdAndContactEmail(
        topicId, contactEmail, isFavouriteTopic);
  }

  @Override
  @Transactional
  public void removeTopicFromFavourite(Integer topicId, UserDetails userDetails) {
    String contactEmail = userDetails.getUsername();
    boolean isNotFavouriteTopic = false;

    if (!topicRepository.existsById(topicId)) {
      throw new TopicNotFoundException(String.format("Topic with id [%d] is not found.", topicId));
    } else if (!topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(
        contactEmail, topicId)) {
      throw new NotSubscribedTopicException(
          "You cannot mark topic as not favourite because you did not subscribe before");
    }

    topicSubscriberRepository.updateFavouriteTopicStatusByTopicIdAndContactEmail(
        topicId, contactEmail, isNotFavouriteTopic);
  }


  @Override
  public boolean hasProhibitionSendingPrivateMessages(Integer topicId) {
    return topicSubscriberRepository.checkIfExistProhibitionSendingPrivateMessage(topicId);
  }

  private boolean isTopicCreator(Integer topicId, String topicCreator) {
    return topicSubscriberRepository.existsByTopicIdAndTopicCreatedBy(topicId, topicCreator);
  }

}
