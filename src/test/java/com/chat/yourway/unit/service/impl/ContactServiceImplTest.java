package com.chat.yourway.unit.service.impl;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.EmailMessageType;
import com.chat.yourway.model.EmailToken;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.service.EmailSenderService;
import com.chat.yourway.service.impl.ContactServiceImpl;
import com.chat.yourway.service.impl.EmailMessageFactoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceImplTest {
    private static final String PATH = "path";
    private static final ArgumentCaptor<EmailToken> EMAIL_TOKEN_CAPTOR = ArgumentCaptor.forClass(EmailToken.class);
    private static final ArgumentCaptor<EmailMessageInfoDto> EMAIL_MESSAGE_INFO_DTO_CAPTOR
            = ArgumentCaptor.forClass(EmailMessageInfoDto.class);
    private static final ArgumentCaptor<EmailMessageDto> EMAIL_MESSAGE_DTO_CAPTOR
            = ArgumentCaptor.forClass(EmailMessageDto.class);

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private EmailTokenRepository emailTokenRepository;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private EmailMessageFactoryServiceImpl emailMessageFactoryService;
    @InjectMocks
    private ContactServiceImpl contactService;

    @Test
    @DisplayName("change password should change password when user passed correct old password")
    public void changePassword_shouldChangePassword_whenUserPassedCorrectOldPassword() {
        // Given
        var oldPassword = "oldPassword";
        var newPassword = "newPassword";
        var contact = Contact.builder()
                .id(1)
                .username("username12353")
                .email("user@gmail.com")
                .password(oldPassword)
                .isActive(true)
                .isPrivate(true)
                .build();
        var encodedPassword = contact.getPassword();
        var request = new ChangePasswordDto(oldPassword, newPassword);

        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // When
        contactService.changePassword(request, contact);

        // Then
        verify(passwordEncoder).matches(oldPassword, encodedPassword);
        verify(passwordEncoder).encode(newPassword);
        verify(contactRepository).changePasswordByUsername(encodedPassword, contact.getUsername());
    }

    @Test
    @DisplayName("change password should throw password OldPasswordsIsNotEqualToNewException when user passed incorrect old password")
    public void changePassword_shouldThrowOldPasswordsIsNotEqualToNewException_whenUserPassedIncorrectOldPassword() {
        // Given
        var contact = Contact.builder()
                .id(1)
                .username("username12353")
                .email("user@gmail.com")
                .password("encodedPassword")
                .isActive(true)
                .isPrivate(true)
                .build();
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedPassword = contact.getPassword();
        var request = new ChangePasswordDto(oldPassword, newPassword);

        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(false);

        // When
        assertThrows(OldPasswordsIsNotEqualToNewException.class, () -> contactService.changePassword(request, contact));

        // Then
        verify(passwordEncoder).matches(oldPassword, encodedPassword);
        verify(passwordEncoder, never()).encode(anyString());
        verify(contactRepository, never()).changePasswordByUsername(anyString(), anyString());
    }

    @Test
    @DisplayName("send email to restore password should send email when user account exists by email")
    public void sendEmailToRestorePassword_shouldSendEmail_whenUserAccountExistsByEmail() {
        // Given
        var username = "username12353";
        var email = "user@gmail.com";
        var emailMessageType = EmailMessageType.RESTORE_PASSWORD;
        var contact = Contact.builder()
                .id(1)
                .username(username)
                .email(email)
                .password("123456")
                .isActive(true)
                .isPrivate(true)
                .build();
        var emailMessage = new EmailMessageDto(email, emailMessageType.getSubject(), emailMessageType.getMessageBody());

        when(contactRepository.findByEmail(email)).thenReturn(Optional.of(contact));
        when(emailMessageFactoryService.generateEmailMessage(any(EmailMessageInfoDto.class))).thenReturn(emailMessage);
        doNothing().when(emailSenderService).sendEmail(any(EmailMessageDto.class));

        // When
        contactService.sendEmailToRestorePassword(email, PATH);

        // Then
        verify(contactRepository).findByEmail(email);
        verify(emailTokenRepository).save(EMAIL_TOKEN_CAPTOR.capture());
        verify(emailMessageFactoryService).generateEmailMessage(EMAIL_MESSAGE_INFO_DTO_CAPTOR.capture());
        verify(emailSenderService).sendEmail(EMAIL_MESSAGE_DTO_CAPTOR.capture());
    }

    @Test
    @DisplayName("send email to restore password should throw ContactNotFoundException when user account does not exist by email")
    public void sendEmailToRestorePassword_shouldThrowContactNotFoundException_whenUserAccountDoesNotExistByEmail() {
        // Given
        var email = "user@gmail.com";

        when(contactRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        assertThrows(ContactNotFoundException.class,
                () -> contactService.sendEmailToRestorePassword(email, PATH));

        // Then
        verify(contactRepository).findByEmail(email);
        verify(emailTokenRepository, never()).save(EMAIL_TOKEN_CAPTOR.capture());
        verify(emailMessageFactoryService, never()).generateEmailMessage(EMAIL_MESSAGE_INFO_DTO_CAPTOR.capture());
        verify(emailSenderService, never()).sendEmail(EMAIL_MESSAGE_DTO_CAPTOR.capture());
    }

    @Test
    @DisplayName("restore password should set new password when user gave correct token")
    public void restorePassword_shouldSetNewPassword_whenUserGaveCorrectToken() {
        // Given
        var newPassword = "newPassword";
        var uuidToken = UUID.randomUUID().toString();
        var contact = Contact.builder()
                .id(1)
                .username("username")
                .email("email")
                .password("123456")
                .isActive(true)
                .isPrivate(true)
                .build();
        var emailToken = EmailToken.builder()
                .token(uuidToken)
                .contact(contact)
                .messageType(EmailMessageType.RESTORE_PASSWORD)
                .build();

        when(emailTokenRepository.findById(uuidToken)).thenReturn(Optional.of(emailToken));

        // When
        contactService.restorePassword(newPassword, uuidToken);

        // Then
        verify(emailTokenRepository).findById(uuidToken);
        verify(passwordEncoder).encode(newPassword);
        verify(emailTokenRepository).delete(EMAIL_TOKEN_CAPTOR.capture());
    }

    @Test
    @DisplayName("restore password should throw EmailTokenNotFoundException when user gave incorrect token")
    public void restorePassword_shouldThrowEmailTokenNotFoundException_whenUserGaveIncorrectToken() {
        // Given
        var newPassword = "newPassword";
        var uuidToken = UUID.randomUUID().toString();

        when(emailTokenRepository.findById(uuidToken)).thenReturn(Optional.empty());

        // When
        assertThrows(EmailTokenNotFoundException.class,
                () -> contactService.restorePassword(newPassword, uuidToken));

        // Then
        verify(emailTokenRepository).findById(uuidToken);
        verify(passwordEncoder, never()).encode(newPassword);
        verify(emailTokenRepository, never()).delete(EMAIL_TOKEN_CAPTOR.capture());
    }
}
