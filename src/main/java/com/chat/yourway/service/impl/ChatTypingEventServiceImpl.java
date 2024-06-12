package com.chat.yourway.service.impl;

import com.chat.yourway.model.redis.ContactOnline;
import com.chat.yourway.service.ChatTypingEventService;
import com.chat.yourway.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatTypingEventServiceImpl implements ChatTypingEventService {

    private final ContactOnlineService contactOnlineService;
    private final ContactService contactService;
    private final NotificationService notificationService;

    @Override
    public void updateTypingEvent(Boolean isTyping, String email) {
        log.info("Start updateTypingEvent isTyping={}, email={}", isTyping, email);
        ContactOnline contactOnline = contactOnlineService.getContactOnline(email);
        if (contactOnline != null) {
            contactOnline.setTypingStatus(isTyping);
        } else {
            contactOnline = new ContactOnline();
            contactOnline.setId(email);
            contactOnline.setTypingStatus(isTyping);
        }
        contactOnline = contactOnlineService.save(contactOnline);
        notificationService.contactChangeStatus(
                contactOnlineService.getOnlineContacts(),
                contactService.findByEmail(email),
                contactOnline);
    }
}
