package com.chat.yourway.service;

import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.mapper.TopicMapper;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.interfaces.TopicService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceImpl implements TopicService {

  private final TopicRepository topicRepository;
  private final TopicMapper topicMapper;

  @Transactional
  @Override
  public TopicResponseDto create(String topicName, String email) {
    log.trace("Started create topic name: {} by contact email: {}", topicName, email);

    Topic topic = topicRepository.save(Topic.builder()
        .topicName(topicName)
        .createdBy(email)
        .createdAt(LocalDateTime.now())
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
    return findById(id).getCreatedBy().equals(email);
  }

}
