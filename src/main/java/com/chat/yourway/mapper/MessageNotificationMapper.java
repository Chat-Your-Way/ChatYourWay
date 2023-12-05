package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import com.chat.yourway.model.event.ContactEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageNotificationMapper {

  @Mapping(target = "unreadMessages", ignore = true, defaultValue = "0")
  @Mapping(target = "status", source = "eventType")
  @Mapping(target = "lastRead", source = "timestamp")
  @Mapping(target = "email", source = "email")
  MessageNotificationResponseDto toNotificationResponseDto(ContactEvent event);

}
