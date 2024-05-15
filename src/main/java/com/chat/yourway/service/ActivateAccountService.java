package com.chat.yourway.service;

import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.model.Contact;

import java.util.UUID;

public interface ActivateAccountService {

  /**
   * Activates a contact account using the provided activation token.
   *
   * @param token The activation token.
   * @throws EmailTokenNotFoundException If the token is not found in the repository.
   */
  void activateAccount(UUID token);

  /**
   * Sends a verification email for activating a contact account.
   *
   * @param contact     The contact to send the verification email.
   * @param clientHost The client host for generating the verifying link.
   */
  void sendVerifyEmail(Contact contact, String clientHost);

}
