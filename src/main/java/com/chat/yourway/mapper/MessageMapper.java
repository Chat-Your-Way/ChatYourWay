package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mapping(target = "topicId", source = "topic.id")
  @Mapping(target = "my", expression = "java(isMyMessage(message, me))")
  MessageResponseDto toResponseDto(Message message, @Context Contact me);

  List<MessageResponseDto> toListResponseDto(List<Message> messages);

  default boolean isMyMessage(Message message, @Context Contact me) {
    return message.getSender().equals(me);
  }
}
