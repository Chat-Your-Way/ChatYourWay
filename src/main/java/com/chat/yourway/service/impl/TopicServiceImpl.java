package com.chat.yourway.service.impl;

import com.chat.yourway.dto.request.TagRequestDto;
import com.chat.yourway.dto.request.TopicPrivateRequestDto;
import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.TopicInfoResponseDto;
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
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final TopicMapper topicMapper;
    private final ContactService contactService;

    @Transactional
    @Override
    public TopicResponseDto create(TopicRequestDto topicRequestDto, String email) {
        log.trace("Started create topic: {} by contact email: {}", topicRequestDto, email);
        Topic topic = createOrUpdateTopic(null, topicRequestDto, email);
        return topicMapper.toResponseDto(topic);
    }

    @Transactional
    @Override
    public TopicResponseDto createPrivate(TopicPrivateRequestDto topicPrivateDto, String email) {
        String sendTo = topicPrivateDto.getSendTo();
        log.trace("Started create private topic by sendTo: {} and creator email: {}", sendTo, email);
        validateRecipientEmail(sendTo);
        String privateName = generatePrivateName(sendTo, email);
        TopicRequestDto topicRequestDto = new TopicRequestDto(privateName, new HashSet<>());

        Topic topic = createOrUpdateTopic(null, topicRequestDto, email);

//    topicSubscriberService.subscribeToTopicById(email, topic.getId());
//    topicSubscriberService.subscribeToTopicById(sendTo, topic.getId());
        return topicMapper.toResponseDto(topic);
    }

    @Transactional
    @Override
    public TopicResponseDto update(UUID topicId, TopicRequestDto topicRequestDto, String email) {
        log.trace("Started update topic: {} by contact email: {}", topicRequestDto, email);
        Topic topic = getTopic(topicId);
        validateCreator(email, topic);
        Topic updatedTopic = createOrUpdateTopic(topic, topicRequestDto, email);
        return topicMapper.toResponseDto(updatedTopic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponseDto findById(UUID id) {
        log.trace("Started findById: {}", id);

        Topic topic = getTopic(id);

        log.trace("Topic id: {} was found", id);
        return topicMapper.toResponseDto(topic);
    }

    @Override
    public TopicResponseDto findByName(String name) {
        log.trace("Started findByName: {}", name);

        Topic topic = getTopicByName(name);

        log.trace("Topic name: {} was found", name);
        return topicMapper.toResponseDto(topic);
    }

    @Override
    public List<TopicInfoResponseDto> findAllPublic() {
        log.trace("Started findAllPublic");
        List<Topic> topics = topicRepository.findAllByScope(TopicScope.PUBLIC);
        log.trace("All public topics was found");
        return topicMapper.toListInfoResponseDto(topics);
    }

    @Override
    public List<TopicResponseDto> findTopicsByTagName(String tagName) {
        log.trace("Started findTopicsByTagName");

        List<Topic> topics = topicRepository.findAllByTagName(tagName);

        log.trace("All Topics by tag name was found");
        return topicMapper.toListResponseDto(topics);
    }

    @Transactional
    @Override
    public void delete(UUID id, String email) {
        log.trace("Started delete emil: {} and topicId: {}", email, id);

        Topic topic = getTopic(id);
        validateCreator(email, topic);

        topic.getTopicSubscribers().stream()
                .forEach(contact -> {
                    contact.getFavoriteTopics().remove(topic);
                    contactService.save(contact);
                });
        topic.setScope(TopicScope.DELETED);
        topic.setTopicSubscribers(Collections.EMPTY_LIST);
        topicRepository.save(topic);
        log.trace("Deleted Topic by creator email: {} and topicId: {}", email, id);
    }

    @Transactional
    @Override
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
    @Override
    public List<TopicResponseDto> findTopicsByTopicName(String topicName) {
        return topicMapper.toListResponseDto(topicRepository.findAllByName(topicName));
    }

    @Override
    public String generatePrivateName(String sendTo, String email) {
        return Stream.of(sendTo, email).sorted().collect(Collectors.joining("-"));
    }

    @Override
    public List<TopicInfoResponseDto> findAllFavouriteTopics(UserDetails userDetails) {
        String contactEmail = userDetails.getUsername();
        Contact contact = contactService.findByEmail(contactEmail);
        return topicMapper.toListInfoResponseDto(
                contact.getFavoriteTopics());
    }

    @Override
    public List<TopicInfoResponseDto> findPopularPublicTopics() {
        return topicMapper.toListInfoResponseDto(topicRepository.findPopularPublicTopics());
    }

    private Topic createOrUpdateTopic(Topic topic, TopicRequestDto topicRequestDto, String email) {
        String topicName = topicRequestDto.getTopicName();
        validateName(topicName);
        Contact contact = contactService.findByEmail(email);

        Set<Tag> tags = addUniqTags(topicRequestDto.getTags());

        if (topic == null) {
            log.trace("Create new topic");
            topic =
                    Topic.builder()
                            .name(topicName)
                            .scope(TopicScope.PUBLIC) //TODO додати створення приватного топіку
                            .createdBy(contact)
                            .tags(tags)
                            .topicSubscribers(List.of(contact))
                            .build();
        } else {
            log.trace("Update topic");
            topic.setName(topicName);
            topic.setTags(tags);
        }

        log.trace("Topic name: {} was saved", topicName);
        return topicRepository.save(topic);
    }

    @Transactional
    @Override
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    public Topic getTopic(UUID topicId) {
        return topicRepository
                .findById(topicId)
                .orElseThrow(
                        () -> {
                            log.warn("Topic id: {} wasn't found", topicId);
                            return new TopicNotFoundException(
                                    String.format("Topic id: %s wasn't found", topicId));
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

    private void validateCreator(String email, Topic topic) {
        if (!isCreator(email, topic)) {
            log.warn("Email: {} wasn't the topic creator", email);
            throw new TopicAccessException(String.format("Email: %s wasn't the topic creator", email));
        }
    }

    private boolean isCreator(String email, Topic topic) {
        if (topic == null) {
            throw new TopicNotFoundException("Topic not found");
        }
        return topic.getCreatedBy().getEmail().equals(email);
    }


    private void validateRecipientEmail(String sendTo) {
        if (!contactService.isEmailExists(sendTo)) {
            log.error("Private topic cannot be created, recipient email={} does not exist", sendTo);
            throw new ContactEmailNotExist(String.format(
                    "Private topic cannot be created because recipient email: %s does not exist", sendTo));
        }
    }


}
