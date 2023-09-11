package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.model.Contact;
import org.springframework.security.core.userdetails.UserDetails;

public interface ContactService {

  /**
   * Creates a new contact.
   *
   * @param contactRequestDto Contact request details.
   * @return {@link Contact} entity.
   * @throws ValueNotUniqException If the provided email or username is already in use.
   */
  Contact create(ContactRequestDto contactRequestDto);

  /**
   * Finds a contact entity by email.
   *
   * @param email Email of the contact.
   * @return {@link Contact} entity.
   * @throws ContactNotFoundException If contact by email wasn't found.
   */
  Contact findByEmail(String email);

  /**
   * Changes a password of contact by email
   *
   * @param password Password of contact
   * @param email Email of contact
   */
  void changePasswordByEmail(String password, String email);

  /**
   * Changes a password of contact by email
   *
   * @param password Password for checking
   * @param encodedPassword Encoded password of contact
   */
  void verifyPassword(String password, String encodedPassword);

  /**
   * Edit contact profile (nickname and avatarId)
   *
   * @param editContactProfileRequestDto Request object for changing data
   * */
  void updateContactProfile(EditContactProfileRequestDto editContactProfileRequestDto, UserDetails userDetails);
}
