package com.chat.yourway.integration.service.impl;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.ContactService;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    private static final String USERNAME = "username12345";

    @Autowired
    private ContactService contactService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ContactRepository contactRepository;

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
        var errorPassword = "errorPassword";
        var newPassword = "newPassword";
        var request = new ChangePasswordDto(errorPassword, newPassword);
        var contact = contactRepository.findByUsername(USERNAME).get();

        // When
        assertThrows(OldPasswordsIsNotEqualToNewException.class, () -> contactService.changePassword(request, contact));

        // Then
        verify(passwordEncoder, never()).encode(anyString());
        verify(contactRepository, never()).changePasswordByUsername(anyString(), anyString());
    }

}
