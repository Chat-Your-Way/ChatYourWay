package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.Topic;
import com.chat.yourway.service.LastMessagesService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Named("BaseMapper")
@Component
@RequiredArgsConstructor
public class BaseMapper {

    private final ContactMapper contactMapper;
    private final MessageMapper messageMapper;
    private final LastMessagesService lastMessagesService;

    @Named("getNamePrivateTopic")
    public String getNamePrivateTopic(Topic topic, @Context Contact me) {
        Optional<Contact> optionalContact = topic.getTopicSubscribers().stream()
                .filter(contact -> !contact.equals(me))
                .findFirst();

        return optionalContact.map(Contact::getNickname).orElse("");
    }

    @Named("getContactPrivateTopic")
    public ContactResponseDto getContactPrivateTopic(Topic topic, @Context Contact me) {
        Optional<Contact> optionalContact = topic.getTopicSubscribers().stream()
                .filter(contact -> !contact.equals(me))
                .findFirst();

        return optionalContact.map(contactMapper::toResponseDto).orElse(null);
    }

    @Named("getUnreadMessages")
    public List<MessageResponseDto> getUnreadMessages(Topic topic, @Context Contact contact) {
        final var unreadMessages = contact.getUnreadMessages();
        return unreadMessages.stream()
                .filter(m -> m.getTopic().getId().equals(topic.getId()))
                .map(messageMapper::toMessageResponseDto)
                .collect(Collectors.toList());
    }

    @Named("getLastMessage")
    public LastMessageResponseDto getLastMessage(Topic topic, @Context Contact contact) {
        List<LastMessageResponseDto> lastMessages =
                lastMessagesService.getLastMessages(List.of(topic.getId()), topic.getScope());
        if (lastMessages.isEmpty()) {
            return null;
        } else {
            return lastMessages.get(0);
        }
    }
}