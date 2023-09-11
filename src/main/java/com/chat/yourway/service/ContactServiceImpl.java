package com.chat.yourway.service;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.interfaces.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    if (contactRepository.existsByEmailIgnoreCase(contactRequestDto.getEmail())) {
      throw new ValueNotUniqException(
          String.format("Email %s already in use", contactRequestDto.getEmail()));
    }

    if (contactRepository.existsByNicknameIgnoreCase(contactRequestDto.getNickname())) {
      throw new ValueNotUniqException(
          String.format("Nickname %s already in use", contactRequestDto.getNickname()));
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
}
