package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.model.Topic;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopicMapper {

  TopicResponseDto toResponseDto(Topic topic);
  List<TopicResponseDto> toListResponseDto(List<Topic> topics);

}
