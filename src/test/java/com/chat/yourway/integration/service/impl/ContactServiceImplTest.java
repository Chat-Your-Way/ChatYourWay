package com.chat.yourway.integration.service.impl;

import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.ContactServiceImpl;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactServiceImpl contactService;

    @Test
    @DisplayName("should update contact profile when user is exist")
    @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
    public void shouldUpdateContactProfile_whenUserIsExist() {
        // Given
        var email = "user@gmail.com";
        var editedNickname = "editedNickname";
        var editedAvatarId = (byte) 2;
        var request = new EditContactProfileRequestDto(editedNickname, editedAvatarId);
        var userDetails = Mockito.mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn(email);

        // When
        contactService.updateContactProfile(request, userDetails);
        var updatedContact = contactRepository.findByEmailIgnoreCase(email).get();

        // Then
        assertAll(
                () -> assertThat(updatedContact)
                        .withFailMessage("Expecting nickname is updated")
                        .extracting(Contact::getNickname)
                        .isEqualTo(editedNickname),
                () -> assertThat(updatedContact)
                        .withFailMessage("Expecting avatar_id is updated")
                        .extracting(Contact::getAvatarId)
                        .isEqualTo(editedAvatarId));
    }

    @Test
    @DisplayName(
            "should throw ContactNotFoundException when user is not exist")
    @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
    public void shouldThrowContactNotFoundException_whenUserIsNotExist() {
        // Given
        var email = "test@example.com";
        var editedNickname = "editedNickname";
        var editedAvatarId = (byte) 2;
        var request = new EditContactProfileRequestDto(editedNickname, editedAvatarId);
        var existingContact = Mockito.mock(Contact.class);
        var userDetails = Mockito.mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn(email);

        // When
        assertThrows(
                ContactNotFoundException.class,
                () -> contactService.updateContactProfile(request, userDetails));

        // Then
        verify(existingContact, never()).setNickname(anyString());
        verify(existingContact, never()).setAvatarId(anyByte());
    }
}
