package com.chat.yourway.service;

import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import com.chat.yourway.model.TopicSubscriber;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.interfaces.ContactService;
import com.chat.yourway.service.interfaces.TopicService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import java.time.LocalDateTime;
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
  private final ContactService contactService;
  private final TopicService topicService;

  @Transactional
  @Override
  public void subscribeToTopic(String email, Integer topicId) {

    if (hesContactSubscribedToTopic(email, topicId)) {
      throw new ContactAlreadySubscribedToTopicException(
          String.format("Contact email: %s already subscribed to the topic id: %s", email,
              topicId));
    }

    Contact contact = contactService.findByEmail(email);
    Topic topic = topicService.findById(topicId);

    topicSubscriberRepository.save(TopicSubscriber.builder()
        .contact(contact)
        .topic(topic)
        .subscribeAt(LocalDateTime.now())
        .unsubscribeAt(null)
        .build());
  }

  @Transactional
  @Override
  public void unsubscribeFromTopic(String email, Integer topicId) {

    var subscriber = findTopicSubscriberHistory(email, topicId).stream()
        .filter(sub -> sub.getUnsubscribeAt() == null)
        .findFirst()
        .orElseThrow(() -> new TopicSubscriberNotFoundException(
            String.format("Contact email: %s wasn't subscribed to the topic id: %s", email,
                topicId)));
    subscriber.setUnsubscribeAt(LocalDateTime.now());

    topicSubscriberRepository.save(subscriber);
  }

  @Override
  public List<TopicSubscriber> findTopicSubscriberHistory(String email, Integer topicId) {
    return topicSubscriberRepository.findAllByContactEmailAndTopicId(email, topicId);
  }

  @Override
  public boolean hesContactSubscribedToTopic(String email, Integer topicId) {
    return findTopicSubscriberHistory(email, topicId).stream()
        .anyMatch(sub -> sub.getUnsubscribeAt() == null);
  }

}
