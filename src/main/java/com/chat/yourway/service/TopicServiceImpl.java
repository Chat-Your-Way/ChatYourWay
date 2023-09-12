package com.chat.yourway.service;

import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.interfaces.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceImpl implements TopicService {

  private final TopicRepository topicRepository;

  public Topic create(String topicName, String email) {
    return topicRepository.save(Topic.builder()
        .topicName(topicName)
        .createdBy(email)
        .build());
  }

  public Topic findById(Integer id) {
    return topicRepository.findById(id)
        .orElseThrow(
            () -> new TopicNotFoundException(String.format("Topic id: %s wasn't found", id)));
  }

}
