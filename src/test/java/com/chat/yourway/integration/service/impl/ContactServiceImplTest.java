package com.chat.yourway.integration.service.impl;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.NoEqualsPasswordException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.ContactService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PostgresExtension.class)
@ExtendWith(RedisExtension.class)
@SpringBootTest
@TestExecutionListeners(value = {
        TransactionalTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
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
    @DatabaseSetup("/dataset/contacts.xml")
    public void shouldChangePassword_whenUserPassedCorrectPassword() {
        var oldPassword = "oldPassword";
        var newPassword = "newPassword";
        var request = new ChangePasswordDto(oldPassword, newPassword);
        var contact = contactRepository.findByUsername(USERNAME).get();

        contactService.changePassword(request, contact);

        var updatedContact = contactRepository.findByUsername(USERNAME);

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

    @DisplayName("should throw password NoEqualsPasswordException when user passed incorrect old password")
    @Test
    @DatabaseSetup("/dataset/contacts.xml")
    public void shouldThrowPasswordsNoEqualsPasswordException_whenUserPassedIncorrectOldPassword() {
        var errorPassword = "errorPassword";
        var newPassword = "newPassword";
        var request = new ChangePasswordDto(errorPassword, newPassword);
        var contact = contactRepository.findByUsername(USERNAME).get();

        assertThrows(NoEqualsPasswordException.class, () -> contactService.changePassword(request, contact));
    }

}
