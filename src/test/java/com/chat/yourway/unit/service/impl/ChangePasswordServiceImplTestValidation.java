package com.chat.yourway.unit.service.impl;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.dto.request.RestorePasswordDto;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.email.EmailMessageType;
import com.chat.yourway.model.email.EmailToken;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.service.ChangePasswordServiceImpl;
import com.chat.yourway.service.EmailSenderService;
import com.chat.yourway.service.EmailMessageFactoryServiceImpl;
import com.chat.yourway.service.interfaces.ContactService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordServiceImplTestValidation {

  private static final String PATH = "path";
  private static final ArgumentCaptor<EmailToken> EMAIL_TOKEN_CAPTOR =
      ArgumentCaptor.forClass(EmailToken.class);
  private static final ArgumentCaptor<EmailMessageInfoDto> EMAIL_MESSAGE_INFO_DTO_CAPTOR =
      ArgumentCaptor.forClass(EmailMessageInfoDto.class);
  private static final ArgumentCaptor<EmailMessageDto> EMAIL_MESSAGE_DTO_CAPTOR =
      ArgumentCaptor.forClass(EmailMessageDto.class);

  @Mock private PasswordEncoder passwordEncoder;
  @Mock private EmailTokenRepository emailTokenRepository;
  @Mock private EmailSenderService emailSenderService;
  @Mock private EmailMessageFactoryServiceImpl emailMessageFactoryService;
  @InjectMocks private ChangePasswordServiceImpl changePasswordService;
  @Spy private ContactService contactService;

  @Test
  @DisplayName("change password should change password when user passed correct old password")
  public void changePassword_shouldChangePassword_whenUserPassedCorrectOldPassword() {
    // Given
    var oldPassword = "oldPassword";
    var newPassword = "newPassword";
    var contact =
        Contact.builder()
            .id(1)
            .nickname("username12353")
            .avatarId((byte) 1)
            .email("user@gmail.com")
            .password(oldPassword)
            .isActive(true)
            .isPrivate(true)
            .build();
    var encodedPassword = contact.getPassword();
    var request = new ChangePasswordDto(oldPassword, newPassword);

    doNothing().when(contactService).verifyPassword(oldPassword, encodedPassword);

    // When
    changePasswordService.changePassword(request, contact);

    // Then
    verify(contactService).verifyPassword(anyString(), anyString());
    verify(contactService).changePasswordByEmail(newPassword, contact.getEmail());
  }

  @Test
  @DisplayName(
      "change password should throw password PasswordsAreNotEqualException when user passed incorrect old password")
  public void
      changePassword_shouldThrowPasswordsAreNotEqualException_whenUserPassedIncorrectOldPassword() {
    // Given
    var contact =
        Contact.builder()
            .id(1)
            .nickname("username12353")
            .avatarId((byte) 1)
            .email("user@gmail.com")
            .password("encodedPassword")
            .isActive(true)
            .isPrivate(true)
            .build();
    String oldPassword = "oldPassword";
    String newPassword = "newPassword";
    String encodedPassword = contact.getPassword();
    var request = new ChangePasswordDto(oldPassword, newPassword);

    doThrow(PasswordsAreNotEqualException.class)
        .when(contactService)
        .verifyPassword(oldPassword, encodedPassword);

    // When
    assertThrows(
        PasswordsAreNotEqualException.class,
        () -> changePasswordService.changePassword(request, contact));

    // Then
    verify(contactService).verifyPassword(oldPassword, encodedPassword);
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  @DisplayName("send email to restore password should send email when user account exists by email")
  public void sendEmailToRestorePassword_shouldSendEmail_whenUserAccountExistsByEmail() {
    // Given
    var username = "username12353";
    var email = "user@gmail.com";
    var emailMessageType = EmailMessageType.RESTORE_PASSWORD;
    var contact =
        Contact.builder()
            .id(1)
            .nickname(username)
            .avatarId((byte) 1)
            .email(email)
            .password("123456")
            .isActive(true)
            .isPrivate(true)
            .build();
    var emailMessage =
        new EmailMessageDto(
            email, emailMessageType.getSubject(), emailMessageType.getMessageBody());

    when(contactService.findByEmail(email)).thenReturn(contact);
    when(emailMessageFactoryService.generateEmailMessage(any(EmailMessageInfoDto.class)))
        .thenReturn(emailMessage);
    doNothing().when(emailSenderService).sendEmail(any(EmailMessageDto.class));

    // When
    changePasswordService.sendEmailToRestorePassword(email, PATH);

    // Then
    verify(contactService).findByEmail(email);
    verify(emailTokenRepository).save(EMAIL_TOKEN_CAPTOR.capture());
    verify(emailMessageFactoryService)
        .generateEmailMessage(EMAIL_MESSAGE_INFO_DTO_CAPTOR.capture());
    verify(emailSenderService).sendEmail(EMAIL_MESSAGE_DTO_CAPTOR.capture());
  }

  @Test
  @DisplayName(
      "send email to restore password should throw ContactNotFoundException when user account does not exist by email")
  public void
      sendEmailToRestorePassword_shouldThrowContactNotFoundException_whenUserAccountDoesNotExistByEmail() {
    // Given
    var email = "user@gmail.com";

    when(contactService.findByEmail(email)).thenThrow(EntityNotFoundException.class);

    // When
    assertThrows(
        EntityNotFoundException.class,
        () -> changePasswordService.sendEmailToRestorePassword(email, PATH));

    // Then
    verify(contactService).findByEmail(email);
    verify(emailTokenRepository, never()).save(EMAIL_TOKEN_CAPTOR.capture());
    verify(emailMessageFactoryService, never())
        .generateEmailMessage(EMAIL_MESSAGE_INFO_DTO_CAPTOR.capture());
    verify(emailSenderService, never()).sendEmail(EMAIL_MESSAGE_DTO_CAPTOR.capture());
  }

  @Test
  @DisplayName("restore password should set new password when user gave correct token")
  public void restorePassword_shouldSetNewPassword_whenUserGaveCorrectToken() {
    // Given
    var newPassword = "newPassword";
    var uuidToken = UUID.randomUUID().toString();
    var restorePasswordDto = new RestorePasswordDto(newPassword, uuidToken);
    var contact =
        Contact.builder()
            .id(1)
            .nickname("username")
            .avatarId((byte) 1)
            .email("email")
            .password("123456")
            .isActive(true)
            .isPrivate(true)
            .build();
    var emailToken =
        EmailToken.builder()
            .token(uuidToken)
            .contact(contact)
            .messageType(EmailMessageType.RESTORE_PASSWORD)
            .build();

    when(emailTokenRepository.findById(uuidToken)).thenReturn(Optional.of(emailToken));

    // When
    changePasswordService.restorePassword(restorePasswordDto);

    // Then
    verify(emailTokenRepository).findById(uuidToken);
    verify(passwordEncoder).encode(newPassword);
    verify(emailTokenRepository).delete(EMAIL_TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName(
      "restore password should throw EmailTokenNotFoundException when user gave incorrect token")
  public void restorePassword_shouldThrowEmailTokenNotFoundException_whenUserGaveIncorrectToken() {
    // Given
    var newPassword = "newPassword";
    var uuidToken = UUID.randomUUID().toString();
    var restorePasswordDto = new RestorePasswordDto(newPassword, uuidToken);

    when(emailTokenRepository.findById(uuidToken)).thenReturn(Optional.empty());

    // When
    assertThrows(
        EmailTokenNotFoundException.class,
        () -> changePasswordService.restorePassword(restorePasswordDto));

    // Then
    verify(emailTokenRepository).findById(uuidToken);
    verify(passwordEncoder, never()).encode(newPassword);
    verify(emailTokenRepository, never()).delete(EMAIL_TOKEN_CAPTOR.capture());
  }
}
