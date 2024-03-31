package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.notification.MessageNotificationResponseDto;
import com.chat.yourway.dto.response.notification.TopicNotificationResponseDto;
import com.chat.yourway.model.event.ContactEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mapping(target = "status", source = "eventType")
  @Mapping(target = "lastRead", source = "timestamp")
  @Mapping(target = "email", source = "email")
  MessageNotificationResponseDto toMessageNotificationResponseDto(ContactEvent event);

  @Mapping(target = "topicId", source = "topicId")
  @Mapping(target = "unreadMessages", source = "unreadMessages")
  @Mapping(target = "lastMessage", source = "lastMessage")
  TopicNotificationResponseDto toTopicNotificationResponseDto(ContactEvent event);

}
