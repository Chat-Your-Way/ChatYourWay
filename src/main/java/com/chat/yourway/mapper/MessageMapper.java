package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.model.Message;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  MessageResponseDto toResponseDto(Message message);

  List<MessageResponseDto> toListResponseDto(List<Message> messages);

}
