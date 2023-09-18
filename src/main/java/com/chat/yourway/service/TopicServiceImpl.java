package com.chat.yourway.service;

import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.interfaces.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
  private final TopicRepository topicRepository;

  @Override
  public List<Topic> findTopicsByTag(Integer tagId) {
    return topicRepository.findAllByTagId(tagId);
  }
}
