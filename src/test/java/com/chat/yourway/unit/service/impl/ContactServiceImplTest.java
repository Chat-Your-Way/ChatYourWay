package com.chat.yourway.unit.service.impl;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.NoEqualsPasswordException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ContactServiceImplTest {
    private Contact contact;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @BeforeEach
    public void setUp() {
        contact = Contact.builder()
                .id(1)
                .username("username12353")
                .email("user@gmail.com")
                .password("encodedPassword")
                .isActive(true)
                .isPrivate(true)
                .build();
    }

    @Test
    public void testChangePassword_Success() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedPassword = contact.getPassword();
        ChangePasswordDto request = new ChangePasswordDto(oldPassword, newPassword);


        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        contactService.changePassword(request, contact);

        verify(passwordEncoder, times(1)).matches(oldPassword, encodedPassword);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(contactRepository, times(1))
                .changePasswordByUsername(encodedPassword, contact.getUsername());
    }

    @Test
    public void testChangePassword_InvalidOldPassword() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedPassword = contact.getPassword();
        ChangePasswordDto request = new ChangePasswordDto(oldPassword, newPassword);

        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(false);

        assertThrows(NoEqualsPasswordException.class, () -> contactService.changePassword(request, contact));

        verify(passwordEncoder, times(1)).matches(oldPassword, encodedPassword);
        verify(passwordEncoder, never()).encode(anyString());
        verify(contactRepository, never()).changePasswordByUsername(anyString(), anyString());
    }
}
