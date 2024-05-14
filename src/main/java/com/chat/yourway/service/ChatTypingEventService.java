package com.chat.yourway.service;

public interface ChatTypingEventService {

  /**
   * Update typing event by user email.
   *
   * @param isTyping typing status.
   * @param email    user email.
   */
  void updateTypingEvent(Boolean isTyping, String email);

}
