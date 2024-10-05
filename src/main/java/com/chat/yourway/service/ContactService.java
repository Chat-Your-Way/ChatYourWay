package com.chat.yourway.service;

import com.chat.yourway.config.security.MyPasswordEncoder;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.request.EditContactProfileRequestDto;
import com.chat.yourway.dto.response.ContactProfileResponseDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.repository.jpa.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.chat.yourway.model.Role.USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;
    private final MyPasswordEncoder myPasswordEncoder;

    @Transactional
    public void save(Contact contact) {
        contactRepository.save(contact);
    }

    @Transactional
    public Contact create(ContactRequestDto contactRequestDto) {
        log.trace("Started create contact, contact email: [{}]", contactRequestDto.getEmail());

        if (isEmailExists(contactRequestDto.getEmail())) {
            log.warn("Email [{}] already in use", contactRequestDto.getEmail());
            throw new ValueNotUniqException(
                    String.format("Email [%s] already in use", contactRequestDto.getEmail())
            );
        }

        Contact contact = Contact.builder()
                        .nickname(contactRequestDto.getNickname())
                        .avatarId(contactRequestDto.getAvatarId())
                        .email(contactRequestDto.getEmail())
                        .password(myPasswordEncoder.encode(contactRequestDto.getPassword()))
                        .isActive(false)
                        .role(USER)
                        .isPermittedSendingPrivateMessage(true)
                        .build();

        contactRepository.save(contact);

        log.info("New contact with email [{}] was created", contactRequestDto.getEmail());
        return contact;
    }

    @Transactional(readOnly = true)
    public Contact findByEmail(String email) {
        log.trace("Started findByEmail: [{}]", email);
        Contact contact = contactRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.warn("Email [{}] wasn't found", email);
                    return new ContactNotFoundException(String.format("Email [%s] wasn't found", email));
                    }
                );

        log.info("Contact was found by email [{}]", email);
        return contact;
    }

    @Transactional
    public void changePasswordByEmail(String password, String email) {
        log.trace("Started change password by email [{}]", email);
        contactRepository.changePasswordByEmail(myPasswordEncoder.encode(password), email);
        log.info("Password was changed by email [{}]", email);
    }

    public void verifyPassword(String password, String encodedPassword) {
        log.trace("Started verify password");

        if (!myPasswordEncoder.matches(password, encodedPassword)) {
            log.warn("Password was not verify");
            throw new PasswordsAreNotEqualException();
        }
        log.info("Password was verified");
    }

    @Transactional
    public void updateContactProfile(EditContactProfileRequestDto editContactProfileRequestDto) {
        log.trace("Started updating contact profile: [{}]", editContactProfileRequestDto);
        Contact contact = getCurrentContact();
        contact.setNickname(editContactProfileRequestDto.getNickname());
        contact.setAvatarId(editContactProfileRequestDto.getAvatarId());

        contactRepository.save(contact);

        log.info("Updated contact by email [{}]", contact.getEmail());
    }

    public boolean isEmailExists(String email) {
        log.trace("Started check is email exists in repository");
        return contactRepository.existsByEmailIgnoreCase(email);
    }

    @Transactional
    public ContactProfileResponseDto getContactProfile() {
        Contact contact = getCurrentContact();
        log.trace("Started get contact profile by email [{}]", contact.getEmail());

        ContactProfileResponseDto responseDto = new ContactProfileResponseDto();

        responseDto.setNickname(contact.getNickname());
        responseDto.setAvatarId(contact.getAvatarId());
        responseDto.setEmail(contact.getEmail());
        responseDto.setHasPermissionSendingPrivateMessage(contact.isPermittedSendingPrivateMessage());

        log.info("Contact profile was got by email [{}]", contact.getEmail());
        return responseDto;
    }

    @Transactional
    public void permitSendingPrivateMessages() {
        Contact contact = getCurrentContact();
        log.trace("Started permit sending private messages by email [{}]", contact.getEmail());
        boolean isPermittedSendingPrivateMessage = true;

        changePermissionSendingPrivateMessages(contact.getEmail(), isPermittedSendingPrivateMessage);
        log.info("Permitted sending private messages by email [{}]", contact.getEmail());
    }

    @Transactional
    public void prohibitSendingPrivateMessages() {
        Contact contact = getCurrentContact();
        log.trace("Started prohibit sending private messages by email [{}]", contact.getEmail());
        boolean isPermittedSendingPrivateMessage = false;

        changePermissionSendingPrivateMessages(contact.getEmail(), isPermittedSendingPrivateMessage);
        log.info("Prohibited sending private messages by email [{}]", contact.getEmail());
    }

    private void changePermissionSendingPrivateMessages(String email, boolean messageByContactEmail) {
        if (!contactRepository.existsByEmailIgnoreCase(email)) {
            throw new ContactNotFoundException(
                    String.format("Contact with email [%s] is not found.", email)
            );
        }

        contactRepository.updatePermissionSendingPrivateMessageByContactEmail(email, messageByContactEmail);
    }

    @Transactional
    public void addUnreadMessageToTopicSubscribers(Contact contact, Message message) {
        List<Contact> topicSubscribers = message.getTopic().getTopicSubscribers()
                .stream()
                .filter(c -> !c.equals(contact))
                .toList();
        for (Contact topicSubscriber : topicSubscribers) {
            topicSubscriber.getUnreadMessages().add(message);
            save(topicSubscriber);
        }
    }

    @Transactional
    public void deleteUnreadMessage(Contact contact, Message message) {
        contact.getUnreadMessages().remove(message);
        save(contact);
    }

    @Transactional
    public Contact getCurrentContact() {
        try {
            Contact principal = (Contact) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return findByEmail(principal.getEmail());
        } catch (Exception e) {
            return null;
        }
    }
}
