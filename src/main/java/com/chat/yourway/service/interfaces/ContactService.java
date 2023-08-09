package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.model.Contact;

public interface ContactService {

  Contact create(ContactRequestDto contactRequestDto);

  Contact findByEmail(String email);
}
