package com.chat.yourway.integration.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.exception.NoEqualsPasswordException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContactServiceImplTest {
    private Contact contact;
    @Autowired
    private ContactService contactService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    public void setUp() {
        contact = Contact.builder()
                .username("username12353")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("oldPassword"))
                .isActive(true)
                .isPrivate(true)
                .build();
    }

    @Test
    public void testChangePassword_Success() {
        ChangePasswordDto request = new ChangePasswordDto("oldPassword", "newPassword");

        contactRepository.save(contact);
        contactService.changePassword(request, contact);

        Contact updatedContact = contactRepository.findByUsername(contact.getUsername())
                .orElseGet(() -> contact);

        assertTrue(passwordEncoder.matches("newPassword", updatedContact.getPassword()));
    }

    @Test
    public void testChangePassword_InvalidOldPassword() {
        ChangePasswordDto request = new ChangePasswordDto("errorPassword", "newPassword");

        contactRepository.save(contact);
        assertThrows(NoEqualsPasswordException.class, () -> contactService.changePassword(request, contact));
    }
}
