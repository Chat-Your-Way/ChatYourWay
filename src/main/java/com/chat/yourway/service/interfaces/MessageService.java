package com.chat.yourway.service.interfaces;

import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.exception.MessageNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

public interface MessageService {
  /**
   * Reports a message identified by its unique message ID, indicating a violation or inappropriate
   * content, associated with the provided user details.
   *
   * @param messageId The unique identifier of the message to be reported.
   * @param userDetails The details of the user reporting the message. This typically includes
   *     information such as user ID, username, or other relevant user data.
   * @throws MessageNotFoundException If messageId is null or negative, or userDetails is null.
   * @throws MessageHasAlreadyReportedException If the user reporting the message does not have the
   *     necessary permissions.
   */
  void reportMessageById(Integer messageId, UserDetails userDetails);
}
