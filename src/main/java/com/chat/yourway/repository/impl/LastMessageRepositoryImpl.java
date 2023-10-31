package com.chat.yourway.repository.impl;

import com.chat.yourway.repository.LastMessageRepository;
import com.chat.yourway.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@RequiredArgsConstructor
public class LastMessageRepositoryImpl implements LastMessageRepository {
  private static final ConcurrentMap<Integer, Integer> lastMessageIdTopic =
      new ConcurrentHashMap<>();

  private final MessageRepository messageRepository;

  @Override
  public void setLastMessageIdTopicId(Integer lastMessageId, Integer topicId) {
    lastMessageIdTopic.put(topicId, lastMessageId);
  }

  @Override
  public Integer getLastMessageIdByTopicId(Integer topicId) {
    return lastMessageIdTopic.getOrDefault(topicId, messageRepository.findLastMessageIdByTopicId(topicId));
  }
}
