package com.chat.yourway.mapper;

import com.chat.yourway.dto.request.ReceivedMessage;
import com.chat.yourway.dto.response.SendMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * {@link MessageMapper}
 *
 * @author Dmytro Trotsenko on 7/22/23
 */

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "sentTime", ignore = true)
    SendMessage toSendMessage(ReceivedMessage sendMessage);

}
