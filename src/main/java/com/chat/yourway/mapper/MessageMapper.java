package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.model.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mapping(target = "topicId", source = "topic.id")
  MessageResponseDto toResponseDto(Message message);

  List<MessageResponseDto> toListResponseDto(List<Message> messages);

}
