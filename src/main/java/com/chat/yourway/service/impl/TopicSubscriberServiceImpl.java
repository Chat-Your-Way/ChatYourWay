package com.chat.yourway.service.impl;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.mapper.ContactMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.jpa.TopicRepository;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.TopicService;
import com.chat.yourway.service.TopicSubscriberService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicSubscriberServiceImpl implements TopicSubscriberService {

    private final TopicRepository topicRepository;

    private final ContactService contactService;
    private final TopicService topicService;
    private final ContactMapper contactMapper;

    @Override
    public void subscribeToTopicById(String email, UUID topicId) {
        log.trace("Started subscribeToTopic, contact email: {}, id: {}", email, topicId);

        Contact contact = contactService.findByEmail(email);
        Topic topic = topicService.getTopic(topicId);
        List<Contact> topicSubscribers = topic.getTopicSubscribers();
        if (!topicSubscribers.contains(contact)) {
            topicSubscribers.add(contact);
            topicService.save(topic);
        }
    }

    @Override
    public void unsubscribeFromTopicById(String email, UUID topicId) {
        log.trace("Started unsubscribeFromTopic, contact email: {}, id: {}", email, topicId);

        Contact contact = contactService.findByEmail(email);
        Topic topic = topicService.getTopic(topicId);
        List<Contact> topicSubscribers = topic.getTopicSubscribers();
        if (topicSubscribers.contains(contact)) {
            topicSubscribers.remove(contact);
            topicService.save(topic);
        }

        log.trace("Contact email: {} was unsubscribed from the Topic id: {}", email, topicId);
    }

    @Override
    public List<ContactResponseDto> findAllSubscribersByTopicId(UUID id) {
        log.trace("Started findAllSubscribersByTopicId id: {}", id);
        Topic topic = topicService.getTopic(id);
        return topic.getTopicSubscribers().stream()
            .map(contactMapper::toResponseDto)
            .toList();
    }

    @Override
    public boolean hasContactSubscribedToTopic(Topic topic, Contact contact) {
        return topic.getTopicSubscribers().contains(contact);
    }

    @Override
    public void addTopicToFavourite(UUID topicId, UserDetails userDetails) {
        String email = userDetails.getUsername();
        Contact contact = contactService.findByEmail(email);
        Topic topic = topicService.getTopic(topicId);

        subscribeToTopicById(email, topicId);
        contact.getFavoriteTopics().add(topic);
        contactService.save(contact);
    }

    @Override
    public void removeTopicFromFavourite(UUID topicId, UserDetails userDetails) {
        String email = userDetails.getUsername();
        Contact contact = contactService.findByEmail(email);
        Topic topic = topicService.getTopic(topicId);

        contact.getFavoriteTopics().remove(topic);
        contactService.save(contact);
    }

    @Override
    public void complainTopic(UUID topicId, UserDetails userDetails) {
        String email = userDetails.getUsername();
        Contact contact = contactService.findByEmail(email);
        Topic topic = topicService.getTopic(topicId);
        List<Contact> topicComplaints = topic.getTopicComplaints();
        if (!topicComplaints.contains(contact)) {
            topic.getTopicComplaints().add(contact);
            topicRepository.save(topic);
        }
    }
}
