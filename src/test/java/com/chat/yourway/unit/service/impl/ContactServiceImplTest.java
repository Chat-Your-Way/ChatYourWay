package com.chat.yourway.unit.service.impl;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.NoEqualsPasswordException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private ContactServiceImpl contactService;

    @Test
    @DisplayName("change password should change password when user passed correct old password")
    public void changePassword_shouldChangePassword_whenUserPassedCorrectOldPassword() {
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
        contactService.changePassword(request, contact);

        verify(passwordEncoder).matches(oldPassword, encodedPassword);
        verify(passwordEncoder).encode(newPassword);
        verify(contactRepository).changePasswordByUsername(encodedPassword, contact.getUsername());
    }

    @Test
    @DisplayName("change password should throw password NoEqualsPasswordException when user passed incorrect old password")
    public void changePassword_shouldThrowPasswordsNoEqualsPasswordException_whenUserPassedIncorrectOldPassword() {
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

        assertThrows(NoEqualsPasswordException.class, () -> contactService.changePassword(request, contact));

        verify(passwordEncoder).matches(oldPassword, encodedPassword);
        verify(passwordEncoder, never()).encode(anyString());
        verify(contactRepository, never()).changePasswordByUsername(anyString(), anyString());
    }
}
