package com.chat.yourway.service.interfaces;

import com.chat.yourway.model.email.EmailSend;

public interface EmailSenderService {

  void sendEmail(EmailSend request);
}
