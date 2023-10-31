package com.chat.yourway.repository.impl;

import com.chat.yourway.repository.ChatTopicSubscriberRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class ChatTopicSubscriberRepositoryImpl implements ChatTopicSubscriberRepository {
  private static final ConcurrentMap<Integer, Set<String>> chatTopicSubEmails =
      new ConcurrentHashMap<>();

  @Override
  public void addSubEmailToTopic(String subEmail, Integer topicId) {
    chatTopicSubEmails.putIfAbsent(topicId, ConcurrentHashMap.newKeySet()).add(subEmail);
  }

  @Override
  public void deleteSubEmailToTopic(String subEmail, Integer topicId) {
    var subsInTopic = chatTopicSubEmails.get(topicId);
    subsInTopic.remove(subEmail);

    if (subsInTopic.isEmpty()) {
      chatTopicSubEmails.remove(topicId);
    }
  }

  @Override
  public Set<String> getSubsWhoOnlineByTopicId(Integer topicId) {
    return chatTopicSubEmails.get(topicId);
  }
}
