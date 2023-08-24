package com.chat.yourway.integration.service.impl;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;
import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.EmailToken;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.repository.EmailTokenRepository;
import com.chat.yourway.service.ContactService;
import com.chat.yourway.service.EmailMessageFactoryService;
import com.chat.yourway.service.EmailSenderService;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith({PostgresExtension.class,
        RedisExtension.class})
@SpringBootTest
@TestExecutionListeners(value = {
        TransactionalTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        ResetMocksTestExecutionListener.class
})
public class ContactServiceImplTest {
    private static final String PATH = "path";
    private static final String USERNAME = "username12345";
    private static final String EMAIL = "user@gmail.com";
    private static final ArgumentCaptor<EmailToken> EMAIL_TOKEN_CAPTOR = ArgumentCaptor.forClass(EmailToken.class);
    private static final ArgumentCaptor<EmailMessageInfoDto> EMAIL_MESSAGE_INFO_DTO_CAPTOR
            = ArgumentCaptor.forClass(EmailMessageInfoDto.class);
    private static final ArgumentCaptor<EmailMessageDto> EMAIL_MESSAGE_DTO_CAPTOR
            = ArgumentCaptor.forClass(EmailMessageDto.class);

    @Autowired
    private ContactService contactService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private EmailTokenRepository emailTokenRepository;
    @MockBean
    private EmailMessageFactoryService emailMessageFactoryService;
    @MockBean
    private EmailSenderService emailSenderService;

    @DisplayName("should change password when user passed correct password")
    @Test
    @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
    public void shouldChangePassword_whenUserPassedCorrectPassword() {
        // Given
        var oldPassword = "oldPassword";
        var newPassword = "newPassword";
        var request = new ChangePasswordDto(oldPassword, newPassword);
        var contact = contactRepository.findByUsername(USERNAME).get();

        // When
        contactService.changePassword(request, contact);
        var updatedContact = contactRepository.findByUsername(USERNAME);

        // Then
        assertAll(
                () -> assertThat(updatedContact)
                        .withFailMessage("Expecting user to exist")
                        .isPresent(),
                () -> assertThat(updatedContact)
                        .withFailMessage("Expecting user to have correct password")
                        .get()
                        .extracting(Contact::getPassword)
                        .matches(password -> passwordEncoder.matches(newPassword, password))
        );
    }

    @DisplayName("should throw password OldPasswordsIsNotEqualToNewException when user passed incorrect old password")
    @Test
    @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
    public void shouldThrowPasswordsOldPasswordsIsNotEqualToNewException_whenUserPassedIncorrectOldPassword() {
        // Given
        var passwordEncoderMock = mock(PasswordEncoder.class);
        var contactRepositoryMock = mock(ContactRepository.class);
        var errorPassword = "errorPassword";
        var newPassword = "newPassword";
        var request = new ChangePasswordDto(errorPassword, newPassword);
        var contact = contactRepository.findByUsername(USERNAME).get();

        // When
        assertThrows(OldPasswordsIsNotEqualToNewException.class, () -> contactService.changePassword(request, contact));

        // Then
        verify(passwordEncoderMock, never()).encode(anyString());
        verify(contactRepositoryMock, never()).changePasswordByUsername(anyString(), anyString());
    }

    @DisplayName("should send email when user account exists by email")
    @Test
    @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
    public void shouldSendEmail_whenUserAccountExistsByEmail() {
        // Given
        var uuidMock = mock(UUID.class);
        var tokenCount = emailTokenRepository.count();
        mockStatic(UUID.class);
        given(UUID.randomUUID()).willReturn(uuidMock);
        var uuidToken = "token";

        // When
        when(uuidMock.toString()).thenReturn(uuidToken);
        doNothing().when(emailSenderService).sendEmail(any(EmailMessageDto.class));
        contactService.sendEmailToRestorePassword(EMAIL, PATH);

        var contact = contactRepository.findByEmail(EMAIL);

        // Then
        verify(emailMessageFactoryService).generateEmailMessage(EMAIL_MESSAGE_INFO_DTO_CAPTOR.capture());
        verify(emailSenderService).sendEmail(EMAIL_MESSAGE_DTO_CAPTOR.capture());

        assertAll(
                () -> assertThat(contact)
                        .withFailMessage("Expecting user to exist")
                        .isPresent(),
                () -> assertThat(emailTokenRepository.count())
                        .withFailMessage("Expecting token count is increased")
                        .isEqualTo(tokenCount + 1),
                () -> assertThat(emailTokenRepository.findById(uuidToken))
                        .withFailMessage("Expecting token is present")
                        .get()
                        .extracting(EmailToken::getContact)
                        .isEqualTo(contact.get())
        );


    }

    @DisplayName("send email to restore password should throw ContactNotFoundException when user account does not exist by email")
    @Test
    @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
    public void sendEmailToRestorePassword_shouldThrowContactNotFoundException_whenUserAccountDoesNotExistByEmail() {
        // Given
        var emailTokenRepositoryMock = mock(EmailTokenRepository.class);
        var notExistUserEmail = "user1@gmail.com";

        // When
        assertThrows(ContactNotFoundException.class,
                () -> contactService.sendEmailToRestorePassword(notExistUserEmail, PATH));

        // Then
        verify(emailTokenRepositoryMock, never()).save(EMAIL_TOKEN_CAPTOR.capture());
        verify(emailMessageFactoryService, never()).generateEmailMessage(EMAIL_MESSAGE_INFO_DTO_CAPTOR.capture());
        verify(emailSenderService, never()).sendEmail(EMAIL_MESSAGE_DTO_CAPTOR.capture());
    }

    @DisplayName("should set new password when user gave correct token")
    @Test
    @DatabaseSetup(value = "/dataset/restore-password-dataset.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/restore-password-dataset.xml", type = DatabaseOperation.DELETE)
    public void shouldSetNewPassword_whenUserGaveCorrectToken() {
        // Given
        var tokenCount = emailTokenRepository.count();
        var oldPassword = contactRepository.findByEmail(EMAIL).get().getPassword();
        var newPassword = "newPassword";
        var uuidToken = "token";

        // When
        contactService.restorePassword(newPassword, uuidToken);

        // Then
        assertAll(
                () -> assertThat(emailTokenRepository.findById(uuidToken))
                        .withFailMessage("Expecting token is deleted")
                        .isEmpty(),
                () -> assertThat(emailTokenRepository.count())
                        .withFailMessage("Expecting token count is decreased")
                        .isEqualTo(tokenCount - 1),
                () -> assertThat(contactRepository.findByEmail(EMAIL))
                        .withFailMessage("Expecting user password is changed")
                        .get()
                        .extracting(Contact::getPassword)
                        .isNotEqualTo(oldPassword)
        );
    }

    @DisplayName("should throw EmailTokenNotFoundException when user gave incorrect token")
    @Test
    @DatabaseSetup(value = "/dataset/restore-password-dataset.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/restore-password-dataset.xml", type = DatabaseOperation.DELETE)
    public void shouldThrowEmailTokenNotFoundException_whenUserGaveIncorrectToken() {
        // Given
        var passwordEncoderMock = mock(PasswordEncoder.class);
        var emailTokenRepositoryMock = mock(EmailTokenRepository.class);
        var newPassword = "newPassword";
        var uuidToken = "UUID";

        // When
        assertThrows(EmailTokenNotFoundException.class,
                () -> contactService.restorePassword(newPassword, uuidToken));

        // Then
        verify(passwordEncoderMock, never()).encode(newPassword);
        verify(emailTokenRepositoryMock, never()).delete(EMAIL_TOKEN_CAPTOR.capture());
    }

}
