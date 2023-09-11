package com.chat.yourway.unit.service.impl;

import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.ContactServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceImplTest {
  @Mock ContactRepository contactRepository;
  @InjectMocks ContactServiceImpl contactService;

  @Test
  @DisplayName("update contact profile should update contact profile when user is exist")
  public void updateContactProfile_shouldUpdateContactProfile_whenUserIsExist() {
    // Given
    var email = "test@example.com";
    var editedUsername = "editedUsername";
    var editedAvatarId = (byte) 2;
    var request = new EditContactProfileRequestDto(editedUsername, editedAvatarId);
    var existingContact = Mockito.mock(Contact.class);
    var userDetails = Mockito.mock(UserDetails.class);

    when(userDetails.getUsername()).thenReturn(email);
    when(contactRepository.findByEmailIgnoreCase(anyString()))
        .thenReturn(Optional.of(existingContact));

    // When
    contactService.updateContactProfile(request, userDetails);

    // Then
    verify(contactRepository).findByEmailIgnoreCase(anyString());
    verify(existingContact).setNickname(anyString());
    verify(existingContact).setAvatarId(anyByte());
  }

  @Test
  @DisplayName(
      "update contact profile should throw ContactNotFoundException when user is not exist")
  public void updateContactProfile_shouldThrowContactNotFoundException_whenUserIsNotExist() {
    // Given
    var email = "test@example.com";
    var editedUsername = "editedUsername";
    var editedAvatarId = (byte) 2;
    var request = new EditContactProfileRequestDto(editedUsername, editedAvatarId);
    var existingContact = Mockito.mock(Contact.class);
    var userDetails = Mockito.mock(UserDetails.class);

    when(userDetails.getUsername()).thenReturn(email);
    when(contactRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

    // When
    assertThrows(
        ContactNotFoundException.class,
        () -> contactService.updateContactProfile(request, userDetails));

    // Then
    verify(contactRepository).findByEmailIgnoreCase(anyString());
    verify(existingContact, never()).setNickname(anyString());
    verify(existingContact, never()).setAvatarId(anyByte());
  }
}
