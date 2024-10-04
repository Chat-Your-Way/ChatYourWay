package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.PrivateTopicInfoResponseDto;
import com.chat.yourway.dto.response.PublicTopicInfoResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface TopicMapper {
    @Mapping(target = "unreadMessages", source = ".", qualifiedByName = {"BaseMapper", "getUnreadMessages"})
    @Mapping(target = "lastMessage", source = ".", qualifiedByName = {"BaseMapper", "getLastMessage"})
    TopicResponseDto toResponseDto(Topic topic, @Context Contact me);
    @Mapping(target = "name", source = ".", qualifiedByName = {"BaseMapper", "getNamePrivateTopic"})
    @Mapping(target = "contact", source = ".", qualifiedByName = {"BaseMapper", "getContactPrivateTopic"})
    @Mapping(target = "unreadMessages", source = ".", qualifiedByName = {"BaseMapper", "getUnreadMessages"})
    @Mapping(target = "lastMessage", source = ".", qualifiedByName = {"BaseMapper", "getLastMessage"})
    PrivateTopicInfoResponseDto toInfoPrivateResponseDto(Topic topic, @Context Contact me);
    @Mapping(target = "unreadMessages", source = ".", qualifiedByName = {"BaseMapper", "getUnreadMessages"})
    @Mapping(target = "lastMessage", source = ".", qualifiedByName = {"BaseMapper", "getLastMessage"})
    PublicTopicInfoResponseDto toInfoPublicResponseDto(Topic topic, @Context Contact me);
    List<PublicTopicInfoResponseDto> toListInfoResponseDto(List<Topic> topics, @Context Contact me);
}