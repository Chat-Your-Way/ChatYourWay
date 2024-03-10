package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import com.chat.yourway.dto.response.TopicNotificationResponseDto;
import java.util.List;

public interface NotificationService {

  /**
   * Notify all topic subscribers by topic id.
   *
   * @param topicId topic id.
   * @return list of notification messages.
   */
  List<MessageNotificationResponseDto> notifyTopicSubscribers(Integer topicId);

  /**
   * Retrieves a list of notifying all public topics.
   *
   * @param email user email.
   * @return A list of public topic's information.
   */
  List<TopicNotificationResponseDto> notifyAllPublicTopicsByEmail(String email);

}
