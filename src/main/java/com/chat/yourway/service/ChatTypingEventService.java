package com.chat.yourway.service;

import com.chat.yourway.model.redis.ContactOnline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatTypingEventService {

    private final ContactOnlineService contactOnlineService;
    private final ContactService contactService;
    private final NotificationService notificationService;

    public void updateTypingEvent(Boolean isTyping, String email) {
        log.info("Start updateTypingEvent isTyping={}, email={}", isTyping, email);

        ContactOnline contactOnline = contactOnlineService.getContactOnline(email);

        contactOnline = getOnline(isTyping, email, contactOnline);

        contactOnline = contactOnlineService.save(contactOnline);

        notificationService.contactChangeStatus(
                contactOnlineService.getOnlineContacts(),
                contactService.findByEmail(email),
                contactOnline
        );
    }

    private ContactOnline getOnline(Boolean isTyping, String email, ContactOnline contactOnline) {
        if (contactOnline != null) {
            contactOnline.setTypingStatus(isTyping);
        } else {
            contactOnline = new ContactOnline();
            contactOnline.setId(email);
            contactOnline.setTypingStatus(isTyping);
        }
        return contactOnline;
    }
}
