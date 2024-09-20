package com.chat.yourway.service;

import com.chat.yourway.dto.request.TagRequestDto;
import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.PrivateTopicInfoResponseDto;
import com.chat.yourway.dto.response.PublicTopicInfoResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.ContactEmailNotExist;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.mapper.TopicMapper;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Tag;
import com.chat.yourway.model.Topic;
import com.chat.yourway.model.TopicScope;
import com.chat.yourway.repository.jpa.TagRepository;
import com.chat.yourway.repository.jpa.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Context;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final TopicMapper topicMapper;
    private final ContactService contactService;
    private final ContactOnlineService contactOnlineService;
    private final NotificationService notificationService;

    @Transactional
    public TopicResponseDto create(TopicRequestDto topicRequestDto) {
        log.trace("Started create topic: {}}", topicRequestDto);
        validateName(topicRequestDto.getTopicName());
        Contact creatorContact = contactService.getCurrentContact();
        Topic topic = Topic.builder()
            .name(topicRequestDto.getTopicName())
            .scope(TopicScope.PUBLIC)
            .contact(creatorContact)
            .topicSubscribers(List.of(creatorContact))
            .tags(addUniqTags(topicRequestDto.getTags()))
            .build();

        save(topic);
        log.trace("Topic name: {} was saved", topic.getName());
        return topicMapper.toResponseDto(topic, creatorContact);
    }

    @Transactional
    public TopicResponseDto update(UUID topicId, TopicRequestDto topicRequestDto) {
        log.trace("Started update topic: {}", topicRequestDto);
        validateName(topicRequestDto.getTopicName());

        Contact contact = contactService.getCurrentContact();
        Topic topic = getTopic(topicId);
        validateCreator(contact, topic);
        topic.setName(topicRequestDto.getTopicName());
        topic.setTags(addUniqTags(topicRequestDto.getTags()));
        Topic updatedTopic = save(topic);

        log.trace("Topic name: {} was updated", topic.getName());
        return topicMapper.toResponseDto(updatedTopic, contact);
    }

    @Transactional(readOnly = true)
    public TopicResponseDto findById(UUID id) {
        log.trace("Started findById: {}", id);
        Topic topic = getTopic(id);
        log.trace("Topic id: {} was found", id);
        return topicMapper.toResponseDto(topic, contactService.getCurrentContact());
    }

    @Transactional(readOnly = true)
    public TopicResponseDto findByName(String name) {
        log.trace("Started findByName: {}", name);
        Topic topic = getTopicByName(name);
        log.trace("Topic name: {} was found", name);
        return topicMapper.toResponseDto(topic, contactService.getCurrentContact());
    }

    public List<PublicTopicInfoResponseDto> findAllPublic() {
        log.trace("Started findAllPublic");
        Contact contact = contactService.getCurrentContact();
        List<Topic> topics = topicRepository.findAllByScope(TopicScope.PUBLIC);
        log.trace("All public topics was found");
        return topicMapper.toListInfoResponseDto(topics, contact);
    }

    public List<PrivateTopicInfoResponseDto> findAllPrivate() {
        log.trace("Started findAllPrivate");
        Contact contact = contactService.getCurrentContact();
        List<Topic> topics = topicRepository.findPrivateTopics(contact);
        log.trace("All private topics was found");

        return toListInfoPrivateResponseDto(topics, contact);
    }

    public List<TopicResponseDto> findTopicsByTagName(String tagName) {
        log.trace("Started findTopicsByTagName");
        List<Topic> topics = topicRepository.findAllByTagName(tagName);
        log.trace("All Topics by tag name was found");
        return toListResponseDto(topics, contactService.getCurrentContact());
    }

    @Transactional
    public void delete(UUID id) {
        log.trace("Started delete Topic: {}", id);

        Topic topic = getTopic(id);
        Contact contact = contactService.getCurrentContact();
        validateCreator(contact, topic);

        topic.getTopicSubscribers().forEach(subscriber -> {
            subscriber.getFavoriteTopics().remove(topic);
            contactService.save(subscriber);
        });
        topic.setScope(TopicScope.DELETED);
        topic.setTopicSubscribers(Collections.EMPTY_LIST);
        topicRepository.save(topic);
        log.trace("Deleted Topic: {}", id);
    }

    @Transactional
    public Set<Tag> addUniqTags(Set<TagRequestDto> tags) {
        log.trace("Started addUniqTags tags: {}", tags);

        Set<String> tagNames = tags.stream()
            .map(tag -> tag.getName().trim().toLowerCase())
            .collect(toSet());

        Set<Tag> existingTags = tagRepository.findAllByNameIn(tagNames);
        log.trace("Found existing tags: {}", existingTags);

        Set<String> existingTagNames = existingTags.stream().map(Tag::getName).collect(toSet());

        Set<Tag> uniqueTags =
            tagNames.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .map(Tag::new)
                .collect(toSet());
        log.trace("Creating new uniq tags: {}", uniqueTags);

        List<Tag> savedTags = tagRepository.saveAll(uniqueTags);
        log.trace("Saved new uniq tags: {}", savedTags);

        existingTags.addAll(savedTags);
        return existingTags;
    }

    @Transactional(readOnly = true)
    public List<TopicResponseDto> findTopicsByTopicName(String topicName) {
        return toListResponseDto(topicRepository.findAllByName(topicName), contactService.getCurrentContact());
    }

    private String generatePrivateName(String sendTo, String email) {
        return UUID.randomUUID().toString();
    }

    public List<PublicTopicInfoResponseDto> findAllFavouriteTopics() {
        Contact contact = contactService.getCurrentContact();
        return toListInfoResponseDto(contact.getFavoriteTopics(), contact);
    }

    public List<PublicTopicInfoResponseDto> findPopularPublicTopics() {
        return topicMapper.toListInfoResponseDto(
                topicRepository.findPopularPublicTopics(), contactService.getCurrentContact()
        );
    }

    public Topic getPrivateTopic(Contact sendToContact, Contact sendFromContact) {
        return topicRepository.findPrivateTopic(sendToContact, sendFromContact).orElseGet(
            () -> {
                Topic newPrivateTopic = Topic.builder()
                    .contact(sendToContact)
                    .name(generatePrivateName(sendToContact.getEmail(), sendFromContact.getEmail()))
                    .scope(TopicScope.PRIVATE)
                    .topicSubscribers(List.of(sendToContact, sendFromContact))
                    .build();
                topicRepository.save(newPrivateTopic);
                return newPrivateTopic;
            });
    }

    @Transactional
    public Topic save(Topic topic) {
        topicRepository.save(topic);

        List<Contact> onlineContacts = contactOnlineService.getOnlineContacts();
        notificationService.topicChange(onlineContacts, topic);

        return topic;
    }

    public Topic getTopic(UUID topicId) {
        return topicRepository
            .findById(topicId)
            .orElseThrow(
                () -> {
                    log.warn("Topic id: {} wasn't found", topicId);
                    return new TopicNotFoundException(String.format("Topic id: %s wasn't found", topicId));
                });
    }

    private Topic getTopicByName(String name) {
        return topicRepository
            .findByName(name)
            .orElseThrow(
                () -> {
                    log.warn("Topic name: {} wasn't found", name);
                    return new TopicNotFoundException(String.format("Topic name: %s wasn't found", name));
                });
    }

    private void validateName(String topicName) {
        if (isTopicNameExists(topicName)) {
            log.warn("Topic name: [{}] already in use", topicName);
            throw new ValueNotUniqException(String.format("Topic name: %s already in use", topicName));
        }
    }

    private boolean isTopicNameExists(String topicName) {
        return topicRepository.existsByName(topicName);
    }

    private void validateCreator(Contact contact, Topic topic) {
        if (!isCreator(contact, topic)) {
            log.warn("Email: {} wasn't the topic creator", contact.getEmail());
            throw new TopicAccessException(String.format("Email: %s wasn't the topic creator", contact.getEmail()));
        }
    }

    private boolean isCreator(Contact contact, Topic topic) {
        if (topic == null) {
            throw new TopicNotFoundException("Topic not found");
        }
        return topic.getContact().equals(contact);
    }

    private List<PublicTopicInfoResponseDto> toListInfoResponseDto(Set<Topic> topics, @Context Contact me) {
        return topics.stream()
                .map(topic -> topicMapper.toInfoPublicResponseDto(topic, me))
                .toList();
    }

    private List<PrivateTopicInfoResponseDto> toListInfoPrivateResponseDto(List<Topic> topics, @Context Contact me) {
        return topics.stream()
                .map(topic -> topicMapper.toInfoPrivateResponseDto(topic, me))
                .toList();
    }

    public List<TopicResponseDto> toListResponseDto(List<Topic> topics, @Context Contact me) {
        return topics.stream()
                .map(topic -> topicMapper.toResponseDto(topic, me))
                .toList();
    }


    private void validateRecipientEmail(String sendTo) {
        if (!contactService.isEmailExists(sendTo)) {
            log.error("Private topic cannot be created, recipient email={} does not exist", sendTo);
            throw new ContactEmailNotExist(String.format(
                "Private topic cannot be created because recipient email: %s does not exist", sendTo));
        }
    }
}
