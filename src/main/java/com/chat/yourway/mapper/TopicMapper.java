package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.TopicInfoResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    TopicResponseDto toResponseDto(Topic topic);

    List<TopicResponseDto> toListResponseDto(List<Topic> topics);

    @Mapping(target = "messages", ignore = true)
    Topic toEntity(TopicResponseDto topicResponseDto);

    @Mapping(target = "name", expression = "java(setNamePrivateTopic(topic, me))")
    TopicInfoResponseDto toInfoPrivateResponseDto(Topic topic, @Context Contact me);

    List<TopicInfoResponseDto> toListInfoPrivateResponseDto(List<Topic> topics);
    List<TopicInfoResponseDto> toListInfoResponseDto(List<Topic> topics);

    List<TopicInfoResponseDto> toListInfoResponseDto(Set<Topic> topics);

    default List<TopicInfoResponseDto> toListInfoPrivateResponseDto(List<Topic> topics, @Context Contact me) {
        return topics.stream()
                .map(topic -> toInfoPrivateResponseDto(topic, me))
                .toList();
    }

    default String setNamePrivateTopic(Topic topic, @Context Contact me) {
        Optional<Contact> optionalContact = topic.getTopicSubscribers().stream()
                .filter(contact -> !contact.equals(me))
                .findFirst();

        if (optionalContact.isPresent()) {
            return optionalContact.get().getNickname();
        } else {
            return "";
        }
    }
}
