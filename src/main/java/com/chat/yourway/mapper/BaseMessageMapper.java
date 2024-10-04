package com.chat.yourway.mapper;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Named("BaseMessageMapper")
@Component
@RequiredArgsConstructor
public class BaseMessageMapper {
    @Named("isMyMessage")
    public boolean isMyMessage(Message message, @Context Contact me) {
        return message.getSender().equals(me);
    }
}