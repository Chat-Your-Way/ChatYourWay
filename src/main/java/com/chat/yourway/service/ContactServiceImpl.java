package com.chat.yourway.service;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.dto.response.ContactProfileResponseDto;
import com.chat.yourway.exception.*;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.interfaces.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

  private final ContactRepository contactRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public Contact create(ContactRequestDto contactRequestDto) {
    log.trace("Started create contact, contact email: {}", contactRequestDto.getEmail());
    if (isEmailExists(contactRequestDto.getEmail())) {
      throw new ValueNotUniqException(
          String.format("Email %s already in use", contactRequestDto.getEmail()));
    }

    return contactRepository.save(
        Contact.builder()
            .nickname(contactRequestDto.getNickname())
            .avatarId(contactRequestDto.getAvatarId())
            .email(contactRequestDto.getEmail())
            .password(passwordEncoder.encode(contactRequestDto.getPassword()))
            .isActive(false)
            .isPrivate(true)
            .role(Role.USER)
            .build());
  }

  @Override
  @Cacheable("contacts")
  public Contact findByEmail(String email) {
    log.trace("Started findByEmail: {}", email);
    return contactRepository
        .findByEmailIgnoreCase(email)
        .orElseThrow(
            () -> new ContactNotFoundException(String.format("Email %s wasn't found", email)));
  }

  @Override
  public void changePasswordByEmail(String password, String email) {
    contactRepository.changePasswordByEmail(passwordEncoder.encode(password), email);
  }

  @Override
  public void verifyPassword(String password, String encodedPassword) {
    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new PasswordsAreNotEqualException();
    }
  }

  @Transactional
  @Override
  public void updateContactProfile(
      EditContactProfileRequestDto editContactProfileRequestDto, UserDetails userDetails) {
    log.trace("Started updating contact profile: {}", editContactProfileRequestDto);
    String email = userDetails.getUsername();
    Contact contact =
        contactRepository
            .findByEmailIgnoreCase(email)
            .orElseThrow(
                () -> new ContactNotFoundException(String.format("Email %s wasn't found", email)));

    contact.setNickname(editContactProfileRequestDto.getNickname());
    contact.setAvatarId(editContactProfileRequestDto.getAvatarId());

    contact = contactRepository.save(contact);
    log.trace("Updated contact: {}", contact);
  }

  @Override
  public boolean isEmailExists(String email) {
    return contactRepository.existsByEmailIgnoreCase(email);
  }

  @Override
  public ContactProfileResponseDto getContactProfile(UserDetails userDetails) {
    String email = userDetails.getUsername();
    Contact contact =
        contactRepository
            .findByEmailIgnoreCase(email)
            .orElseThrow(
                () -> new ContactNotFoundException(String.format("Email %s wasn't found", email)));
    ContactProfileResponseDto responseDto = new ContactProfileResponseDto();

    responseDto.setNickname(contact.getNickname());
    responseDto.setAvatarId(contact.getAvatarId());
    responseDto.setEmail(email);
    responseDto.setHasPermissionSendingPrivateMessage(contact.isPermittedSendingPrivateMessage());

    return responseDto;
  }

  @Override
  @Transactional
  public void permitSendingPrivateMessages(UserDetails userDetails) {
    boolean isPermittedSendingPrivateMessage = true;

    changePermissionSendingPrivateMessages(userDetails, isPermittedSendingPrivateMessage);
  }

  @Override
  @Transactional
  public void prohibitSendingPrivateMessages(UserDetails userDetails) {
    boolean isPermittedSendingPrivateMessage = false;

    changePermissionSendingPrivateMessages(userDetails, isPermittedSendingPrivateMessage);
  }

  private void changePermissionSendingPrivateMessages(
      UserDetails userDetails, boolean isPermittedSendingPrivateMessage) {
    String contactEmail = userDetails.getUsername();

    if (!contactRepository.existsByEmailIgnoreCase(contactEmail)) {
      throw new ContactNotFoundException(
          String.format("Contact with email [%s] is not found.", contactEmail));
    }

    contactRepository.updatePermissionSendingPrivateMessageByContactEmail(
        contactEmail, isPermittedSendingPrivateMessage);
  }
}
