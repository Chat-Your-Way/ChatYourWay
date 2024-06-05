package com.chat.yourway.service.impl;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.redis.ContactOnline;
import com.chat.yourway.repository.redis.ContactOnlineRedisRepository;
import com.chat.yourway.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactOnlineService {

    private final ContactOnlineRedisRepository contactOnlineRedisRepository;
    private final ContactService contactService;

    public void setUserOnline(String contactEmail) {
        setUserOnline(contactEmail, null);
    }

    public void setUserOnline(String contactEmail, UUID topicId) {
        ContactOnline contactOnline = ContactOnline.builder()
                .id(contactEmail)
                .timestamp(LocalDateTime.now())
                .topicId(topicId)
                .build();
        contactOnlineRedisRepository.save(contactOnline);
    }

    public void setUserOffline(String contactEmail) {
        contactOnlineRedisRepository.deleteById(contactEmail);
    }

    public List<Contact> getOnlineUsersByTopicId(UUID topicId) {
        List<Contact> result = new ArrayList<>();
        List<ContactOnline> contactOnlines = contactOnlineRedisRepository.findAllByTopicId(topicId);
        for (ContactOnline contactOnline : contactOnlines) {
            result.add(contactService.findByEmail(contactOnline.getId()));
        }
        return result;
    }
}
