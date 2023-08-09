package com.chat.yourway.service.interfaces;

import com.chat.yourway.model.Contact;
import jakarta.servlet.http.HttpServletRequest;

public interface ActivateAccountService {

  void activateAccount(String token);

  void sendVerifyEmail(Contact contact, HttpServletRequest httpRequest);

}
