package com.chat.yourway.service.interfaces;

import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.model.Contact;
import jakarta.servlet.http.HttpServletRequest;

public interface ActivateAccountService {

  /**
   * Activates a contact account using the provided activation token.
   *
   * @param token The activation token.
   * @throws EmailTokenNotFoundException If the token is not found in the repository.
   */
  void activateAccount(String token);

  /**
   * Sends a verification email for activating a contact account.
   *
   * @param contact     The contact to send the verification email.
   * @param httpRequest HttpServletRequest.
   */
  void sendVerifyEmail(Contact contact, HttpServletRequest httpRequest);

}
