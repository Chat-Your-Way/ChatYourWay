package com.chat.yourway.unit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.mapper.TopicMapperImpl;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.TagRepository;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.TopicServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {

  @Mock
  TopicRepository topicRepository;
  @Mock
  TagRepository tagRepository;

  @InjectMocks
  TopicServiceImpl topicService;

  @Captor
  ArgumentCaptor<Topic> topicCaptor;

  @BeforeEach
  void init() {
    TopicMapperImpl topicMapper = new TopicMapperImpl();
    ReflectionTestUtils.setField(topicService, "topicMapper", topicMapper);
  }

  @Test
  @DisplayName("create should create a new topic when user passes correct data")
  public void create_shouldCreateNewTopic_whenUserPassesCorrectData() {
    // Given
    var topic = getTopics().get(0);
    TopicRequestDto topicRequestDto = new TopicRequestDto(topic.getTopicName(), new HashSet<>());

    when(topicRepository.save(any(Topic.class))).thenReturn(topic);
    when(tagRepository.saveAll(any())).thenReturn(new ArrayList<>());

    // When
    TopicResponseDto topicResponseDto = topicService.create(topicRequestDto, topic.getCreatedBy());

    // Then
    assertNotNull(topicResponseDto);
    verify(topicRepository, times(1)).save(topicCaptor.capture());
    assertTopicEquals(topic, topicResponseDto);
  }

  @Test
  @DisplayName("findById should return TopicResponseDto when topic exists")
  public void findById_shouldReturnTopicResponseDto_whenTopicExists() {
    // Given
    var topic = getTopics().get(0);

    when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));

    // When
    TopicResponseDto topicResponseDto = topicService.findById(topic.getId());

    // Then
    assertNotNull(topicResponseDto);
    Mockito.verify(topicRepository, Mockito.times(1)).findById(anyInt());
    assertTopicEquals(topic, topicResponseDto);
  }

  @Test
  @DisplayName("findById should throw TopicNotFoundException when topic does not exist")
  public void findById_shouldThrowTopicNotFoundException_whenTopicIsNotExist() {
    // Given
    Integer id = 999;

    when(topicRepository.findById(id)).thenReturn(Optional.empty());

    // When and Then
    assertThrows(TopicNotFoundException.class, () -> topicService.findById(id));

    Mockito.verify(topicRepository, Mockito.times(1)).findById(anyInt());
  }

  @Test
  @DisplayName("findAll should return a list of TopicResponseDto")
  public void findAll_shouldReturnListOfTopicResponseDto() {
    // Given
    List<Topic> topics = getTopics();

    when(topicRepository.findAll()).thenReturn(topics);

    // When
    List<TopicResponseDto> topicResponseDtos = topicService.findAll();

    // Then
    assertThat(topicResponseDtos).isNotNull();
    assertThat(topicResponseDtos).hasSize(2);
    assertTopicEquals(topics.get(0), topicResponseDtos.get(0));
    assertTopicEquals(topics.get(1), topicResponseDtos.get(1));
  }

  @Test
  @DisplayName("findAll should return empty list of TopicResponseDto")
  public void findAll_shouldReturnEmptyListOfTopicResponseDto() {
    // Given
    List<Topic> emptyList = new ArrayList<>();

    when(topicRepository.findAll()).thenReturn(emptyList);

    // When
    List<TopicResponseDto> topicResponseDtos = topicService.findAll();

    // Then
    assertThat(topicResponseDtos).isNotNull();
    assertThat(topicResponseDtos).isEmpty();
  }

  @Test
  @DisplayName("deleteByCreator should delete topic by Id and Contact email")
  public void deleteByCreator_shouldDeleteTopicByIdAndContactEmail() {
    // Given
    Topic topic = getTopics().get(0);
    var id = topic.getId();
    var createdBy = topic.getCreatedBy();

    when(topicRepository.existsByIdAndCreatedBy(id, createdBy)).thenReturn(true);

    // When
    topicService.deleteByCreator(id, createdBy);

    // Then
    verify(topicRepository, times(1)).deleteById(id);
  }

  @Test
  @DisplayName("deleteByCreator should throw TopicAccessException")
  public void deleteByCreator_shouldThrowTopicAccessException() {
    // Given
    Topic topic = getTopics().get(1);
    var differentId = 1;
    var createdBy = topic.getCreatedBy();

    when(topicRepository.existsByIdAndCreatedBy(differentId, createdBy)).thenReturn(false);

    // When and Then
    assertThrows(TopicAccessException.class,
        () -> topicService.deleteByCreator(differentId, createdBy));

    Mockito.verify(topicRepository, Mockito.times(1)).existsByIdAndCreatedBy(anyInt(), anyString());
  }

  private List<Topic> getTopics() {
    Topic topic1 = Topic.builder()
        .id(1)
        .topicName("Topic")
        .createdBy("test-topic@gmail.com")
        .createdAt(LocalDateTime.now())
        .tags(new HashSet<>())
        .build();

    Topic topic2 = Topic.builder()
        .id(2)
        .topicName("Topic2")
        .createdBy("test-topic2@gmail.com")
        .createdAt(LocalDateTime.now())
        .tags(new HashSet<>())
        .build();

    return Arrays.asList(topic1, topic2);
  }

  private void assertTopicEquals(Topic topic, TopicResponseDto topicResponseDto) {
    assertThat(topicResponseDto.getTopicName()).isEqualTo(topic.getTopicName());
    assertThat(topicResponseDto.getCreatedBy()).isEqualTo(topic.getCreatedBy());
    assertThat(topicResponseDto.getCreatedAt()).isEqualTo(topic.getCreatedAt());
    assertThat(topicResponseDto.getTopicSubscribers()).isNull();
  }

}
