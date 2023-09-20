package com.chat.yourway.service;

import static java.util.stream.Collectors.*;

import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.mapper.TopicMapper;
import com.chat.yourway.model.Tag;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.TagRepository;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.interfaces.TopicService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceImpl implements TopicService {

  private final TopicRepository topicRepository;
  private final TagRepository tagRepository;
  private final TopicMapper topicMapper;

  @Transactional
  @Override
  public TopicResponseDto create(TopicRequestDto topicRequestDto, String email) {
    String topicName = topicRequestDto.getTopicName();
    log.trace("Started create topic name: {} by contact email: {}", topicName, email);

    Set<Tag> tags = addTags(topicRequestDto.getTags());
    log.trace("Getting tags: {} for topic name: {}", tags, topicName);

    Topic topic = topicRepository.save(Topic.builder()
        .topicName(topicName)
        .createdBy(email)
        .createdAt(LocalDateTime.now())
        .tags(tags)
        .build());

    log.trace("New Topic name: {} was created", topicName);
    return topicMapper.toResponseDto(topic);
  }

  @Override
  public TopicResponseDto findById(Integer id) {
    log.trace("Started findById: {}", id);

    Topic topic = topicRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Topic id: {} wasn't found", id);
          return new TopicNotFoundException(String.format("Topic id: %s wasn't found", id));
        });

    log.trace("Topic id: {} was found", id);
    return topicMapper.toResponseDto(topic);
  }

  @Override
  public List<TopicResponseDto> findAll() {
    log.trace("Started findAll");

    List<Topic> topics = topicRepository.findAll();

    log.trace("All Topics was found");
    return topicMapper.toListResponseDto(topics);
  }

  @Override
  public List<TopicResponseDto> findTopicsByTag(Integer tagId) {
    log.trace("Started findTopicsByTag");

    List<Topic> topics = topicRepository.findAllByTagId(tagId);

    log.trace("All Topics by tag was found");
    return topicMapper.toListResponseDto(topics);
  }

  @Override
  public void deleteByCreator(Integer id, String email) {
    log.trace("Started deleteByCreator emil: {} and topicId: {}", email, id);

    if (!isCreator(id, email)) {
      log.warn("Email: {} wasn't the topic creator", email);
      throw new TopicAccessException(String.format("Email: %s wasn't the topic creator", email));
    }

    topicRepository.deleteById(id);
    log.trace("Deleted Topic by creator email: {} and topicId: {}", email, id);
  }

  private boolean isCreator(Integer id, String email) {
    log.trace("Checking if contact email: {} is topic creator, topicId: {}", email, id);
    return topicRepository.existsByIdAndCreatedBy(id, email);
  }

  private Set<Tag> addTags(Set<String> tags) {
    log.trace("Started addTags tags: {}", tags);

    Set<String> tagNames = tags.stream()
        .map(String::toLowerCase)
        .collect(toSet());

    Set<Tag> existingTags = tagRepository.findAllByNameIn(tags);
    log.trace("Found existing tags: {}", existingTags);

    Set<String> existingTagNames = existingTags.stream()
        .map(Tag::getName)
        .collect(toSet());

    Set<Tag> uniqueTags = tagNames.stream()
        .filter(tag -> !existingTagNames.contains(tag))
        .map(Tag::new)
        .collect(toSet());
    log.trace("Creating new uniq tags: {}", uniqueTags);

    List<Tag> savedTags = tagRepository.saveAll(uniqueTags);
    log.trace("Saved new uniq tags: {}", savedTags);

    existingTags.addAll(savedTags);
    return existingTags;
  }

}
