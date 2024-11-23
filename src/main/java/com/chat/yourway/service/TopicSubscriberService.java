package com.chat.yourway.service;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.mapper.ContactMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.jpa.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicSubscriberService {

    private final TopicRepository topicRepository;
    private final ContactService contactService;
    private final TopicService topicService;
    private final ContactMapper contactMapper;

    public void subscribeToTopicById(UUID topicId) {
        Contact contact = contactService.getCurrentContact();
        Topic topic = topicService.getTopic(topicId);
        List<Contact> topicSubscribers = topic.getTopicSubscribers();
        if (!topicSubscribers.contains(contact)) {
            topicSubscribers.add(contact);
            topicService.save(topic);
        }
    }

    public void unsubscribeFromTopicById(UUID topicId) {
        Contact contact = contactService.getCurrentContact();
        Topic topic = topicService.getTopic(topicId);
        List<Contact> topicSubscribers = topic.getTopicSubscribers();
        if (topicSubscribers.contains(contact)) {
            topicSubscribers.remove(contact);
            topicService.save(topic);
        }
    }

    public List<ContactResponseDto> findAllSubscribersByTopicId(UUID id) {
        return topicService.getTopic(id).getTopicSubscribers().stream()
                .map(contactMapper::toResponseDto)
                .toList();
    }

    public boolean hasContactSubscribedToTopic(Topic topic, Contact contact) {
        return topic.getTopicSubscribers().contains(contact);
    }

    public void addTopicToFavourite(UUID topicId) {
        Contact contact = contactService.getCurrentContact();
        Topic topic = topicService.getTopic(topicId);

        subscribeToTopicById(topicId);
        contact.getFavoriteTopics().add(topic);
        contactService.save(contact);
    }

    public void removeTopicFromFavourite(UUID topicId) {
        Contact contact = contactService.getCurrentContact();
        Topic topic = topicService.getTopic(topicId);

        contact.getFavoriteTopics().remove(topic);
        contactService.save(contact);
    }

    public void complainTopic(UUID topicId) {
        Contact contact = contactService.getCurrentContact();
        Topic topic = topicService.getTopic(topicId);
        List<Contact> topicComplaints = topic.getTopicComplaints();
        if (!topicComplaints.contains(contact)) {
            topic.getTopicComplaints().add(contact);
            topicRepository.save(topic);
        }
    }
}
