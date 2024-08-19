package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface MessageMapper {
  @Mapping(target = "topicId", source = "topic.id")
  @Mapping(target = "my", source = ".", qualifiedByName = {"BaseMapper", "isMyMessage"})
  MessageResponseDto toResponseDto(Message message, @Context Contact me);
}