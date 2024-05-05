package com.chat.yourway.service.interfaces;

public interface ChatTypingEventService {

  /**
   * Get typing event by user email.
   *
   * @param isTyping typing status.
   * @param email    user email.
   */
  void getTypingEvent(Boolean isTyping, String email);

}
