package com.chat.yourway.service;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.interfaces.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    if (contactRepository.existsByUsernameIgnoreCase(contactRequestDto.getUsername())) {
      throw new ValueNotUniqException(
          String.format("Username %s already in use", contactRequestDto.getUsername()));
    }
    return contactRepository.save(
        Contact.builder()
            .username(contactRequestDto.getUsername())
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
}
