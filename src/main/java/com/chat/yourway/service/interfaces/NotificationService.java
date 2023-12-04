package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import java.util.List;

public interface NotificationService {

  /**
   * Notify all topic subscribers by topic id.
   *
   * @param topicId topic id.
   * @return list of notification messages.
   */
  List<MessageNotificationResponseDto> notifyTopicSubscribers(Integer topicId);

}
