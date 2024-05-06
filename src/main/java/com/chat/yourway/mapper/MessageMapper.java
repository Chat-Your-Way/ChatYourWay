package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.model.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mapping(source = "sender.nickname", target = "sentFromNickname")
  @Mapping(source = "receiver.nickname", target = "sendToNickname",
      defaultExpression = "java(message.getReceiver() != null ? message.getReceiver().getNickname() : null )"
  )
  MessageResponseDto toResponseDto(Message message);

  List<MessageResponseDto> toListResponseDto(List<Message> messages);

}
