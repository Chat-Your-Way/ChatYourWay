package com.chat.yourway.service;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.exception.ServiceException;
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

    if (contactRepository.existsByEmail(contactRequestDto.getEmail())) {
      throw new ServiceException(CONFLICT,
          String.format("Email %s already in use", contactRequestDto.getEmail()));
    }

    if (contactRepository.existsByUsername(contactRequestDto.getUsername())) {
      throw new ServiceException(CONFLICT,
          String.format("Username %s already in use", contactRequestDto.getUsername()));
    }

    return contactRepository.save(Contact.builder()
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
    return contactRepository.findByEmail(email)
        .orElseThrow(
            () -> new ServiceException(NOT_FOUND, String.format("Email %s wasn't found", email)));
  }

}
