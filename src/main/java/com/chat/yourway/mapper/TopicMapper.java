package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.dto.response.PrivateTopicInfoResponseDto;
import com.chat.yourway.dto.response.PublicTopicInfoResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = "spring", uses = ContactMapper.class)
public abstract class TopicMapper {

    @Autowired
    private ContactMapper contactMapper;

    public abstract TopicResponseDto toResponseDto(Topic topic);

    public abstract List<TopicResponseDto> toListResponseDto(List<Topic> topics);

    @Mapping(target = "messages", ignore = true)
    public abstract Topic toEntity(TopicResponseDto topicResponseDto);

    @Mapping(target = "name", expression = "java(getNamePrivateTopic(topic, me))")
    @Mapping(target = "contact", expression = "java(getContactPrivateTopic(topic, me))")
    public abstract PrivateTopicInfoResponseDto toInfoPrivateResponseDto(Topic topic, @Context Contact me);

    public abstract List<PrivateTopicInfoResponseDto> toListInfoPrivateResponseDto(List<Topic> topics);

    public abstract List<PublicTopicInfoResponseDto> toListInfoResponseDto(List<Topic> topics);

    public abstract List<PublicTopicInfoResponseDto> toListInfoResponseDto(Set<Topic> topics);

    public List<PrivateTopicInfoResponseDto> toListInfoPrivateResponseDto(List<Topic> topics, @Context Contact me) {
        return topics.stream()
                .map(topic -> toInfoPrivateResponseDto(topic, me))
                .toList();
    }

    public  String getNamePrivateTopic(Topic topic, @Context Contact me) {
        Optional<Contact> optionalContact = topic.getTopicSubscribers().stream()
                .filter(contact -> !contact.equals(me))
                .findFirst();

        return optionalContact.map(Contact::getNickname).orElse("");
    }

    public ContactResponseDto getContactPrivateTopic(Topic topic,
                                                      @Context Contact me) {
        Optional<Contact> optionalContact = topic.getTopicSubscribers().stream()
                .filter(contact -> !contact.equals(me))
                .findFirst();

        if (optionalContact.isPresent()) {
            return contactMapper.toResponseDto(optionalContact.get());
        } else {
            return null;
        }
    }
}
