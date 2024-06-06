package com.chat.yourway.service.impl;

import com.chat.yourway.model.redis.ContactOnline;
import com.chat.yourway.service.ChatTypingEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatTypingEventServiceImpl implements ChatTypingEventService {

    private final ContactOnlineService contactOnlineService;

    @Override
    public void updateTypingEvent(Boolean isTyping, String email) {
        log.info("Start updateTypingEvent isTyping={}, email={}", isTyping, email);
        ContactOnline contactOnline = contactOnlineService.getContactOnline(email);
        if (contactOnline != null) {
            contactOnline.setTypingStatus(isTyping);
            contactOnlineService.save(contactOnline);
        }
        //TODO повідомлення кому?
    }
}
