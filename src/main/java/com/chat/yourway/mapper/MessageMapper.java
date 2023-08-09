package com.chat.yourway.mapper;

import com.chat.yourway.dto.request.ReceivedMessageDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mapping(target = "sentTime", ignore = true)
  MessageResponseDto toSendMessage(ReceivedMessageDto sendMessage);

}
