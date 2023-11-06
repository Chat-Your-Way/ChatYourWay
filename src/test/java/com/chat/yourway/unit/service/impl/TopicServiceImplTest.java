package com.chat.yourway.unit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.dto.request.TopicPrivateRequestDto;
import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.TagResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.mapper.TopicMapperImpl;
import com.chat.yourway.model.Tag;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.TagRepository;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.TopicServiceImpl;
import com.chat.yourway.service.interfaces.ContactService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {

  @Mock
  TopicRepository topicRepository;
  @Mock
  TagRepository tagRepository;

  @Spy
  TopicSubscriberService topicSubscriberService;
  @Mock
  ContactService contactService;

  @Spy
  @InjectMocks
  TopicServiceImpl topicService;

  private final Integer NOT_EXISTED_TOPIC_ID = 99;
  private final String NOT_EXISTED_EMAIL = "user@example.com";

  @BeforeEach
  void init() {
    TopicMapperImpl topicMapper = new TopicMapperImpl();
    ReflectionTestUtils.setField(topicService, "topicMapper", topicMapper);
  }

  @Test
  @DisplayName("create should create a new topic when user passes correct data")
  public void create_shouldCreateNewTopic_whenUserPassesCorrectData() {
    // Given
    Topic topic = getTopics().get(0);
    String topicName = topic.getTopicName();
    String email = topic.getCreatedBy();
    Set<Tag> expectedTags = topic.getTags();
    Set<String> newTags = expectedTags.stream()
        .map(Tag::getName)
        .collect(Collectors.toSet());
    TopicRequestDto topicRequestDto = new TopicRequestDto(topicName, newTags);

    when(topicRepository.save(any(Topic.class))).thenReturn(topic);
    when(topicService.addUniqTags(newTags)).thenReturn(expectedTags);

    // When
    TopicResponseDto topicResponseDto = topicService.create(topicRequestDto, email);

    // Then
    assertNotNull(topicResponseDto);
    assertTopicEquals(topic, topicResponseDto);
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  @DisplayName("createPrivate should create a new private topic when user passes correct data")
  public void createPrivate_shouldCreateNewPrivateTopic_whenUserPassesCorrectData() {
    // Given
    Topic topic = getTopics().get(0);
    topic.setTopicName("abc@gmail.com<->test-topic@gmail.com");
    topic.setIsPublic(false);
    String email = topic.getCreatedBy();
    String sentTo = "abc@gmail.com";
    TopicPrivateRequestDto topicPrivateRequestDto = new TopicPrivateRequestDto(sentTo);

    when(topicRepository.save(any(Topic.class))).thenReturn(topic);
    when(contactService.isEmailExists("abc@gmail.com")).thenReturn(true);

    // When
    TopicResponseDto topicResponseDto = topicService.createPrivate(topicPrivateRequestDto, email);

    // Then
    assertNotNull(topicResponseDto);
    assertTopicEquals(topic, topicResponseDto);
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  @DisplayName("create should throw ValueNotUniqException when topic name already exists")
  public void create_shouldThrowValueNotUniqException_whenTopicNameAlreadyExists() {
    // Given
    Topic topic = getTopics().get(0);
    String topicName = topic.getTopicName();
    String email = topic.getCreatedBy();
    TopicRequestDto topicRequestDto = new TopicRequestDto(topicName, Set.of("tag1", "tag2"));

    when(topicRepository.existsByTopicName(topicName)).thenReturn(true);

    // When / Then
    assertThrows(ValueNotUniqException.class, () -> topicService.create(topicRequestDto, email));
  }

  @Test
  @DisplayName("update should update topic when valid data provided")
  public void update_shouldUpdateTopic_whenValidDataProvided() {
    // Given
    Topic topic = getTopics().get(0);
    Integer topicId = topic.getId();
    String topicName = topic.getTopicName();
    String email = topic.getCreatedBy();
    Set<Tag> expectedTags = topic.getTags();
    Set<String> newTags = expectedTags.stream()
        .map(Tag::getName)
        .collect(Collectors.toSet());
    TopicRequestDto topicRequestDto = new TopicRequestDto(topicName, newTags);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
    when(topicService.addUniqTags(newTags)).thenReturn(expectedTags);
    when(topicRepository.save(any(Topic.class))).thenReturn(topic);

    // When
    TopicResponseDto topicResponseDto = topicService.update(topicId, topicRequestDto, email);

    // Then
    assertNotNull(topicResponseDto);
    assertTopicEquals(topic, topicResponseDto);
    verify(topicRepository, times(1)).findById(anyInt());
    verify(topicService, times(1)).addUniqTags(anySet());
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  @DisplayName("update should throw TopicNotFoundException when topic ID not found")
  public void update_shouldThrowTopicNotFoundException_whenTopicIdNotFound() {
    // Given
    Topic topic = getTopics().get(0);
    String topicName = topic.getTopicName();
    String email = topic.getCreatedBy();
    TopicRequestDto topicRequestDto = new TopicRequestDto(topicName, Set.of("tag1", "tag2"));

    when(topicRepository.findById(NOT_EXISTED_TOPIC_ID)).thenReturn(Optional.empty());

    // When / Then
    assertThrows(TopicNotFoundException.class,
        () -> topicService.update(NOT_EXISTED_TOPIC_ID, topicRequestDto, email));
  }

  @Test
  @DisplayName("update should throw TopicAccessException when user is not the creator")
  public void update_shouldThrowTopicAccessException_whenUserIsNotTheCreator() {
    // Given
    Topic topic = getTopics().get(0);
    Integer topicId = topic.getId();
    String topicName = topic.getTopicName();
    TopicRequestDto topicRequestDto = new TopicRequestDto(topicName, Set.of("tag1", "tag2"));

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

    // When / Then
    assertThrows(TopicAccessException.class,
        () -> topicService.update(topicId, topicRequestDto, NOT_EXISTED_EMAIL));
  }

  @Test
  @DisplayName("update should throw ValueNotUniqException when topic name already exists")
  public void update_shouldThrowValueNotUniqException_whenTopicNameAlreadyExists() {
    // Given
    Topic topic = getTopics().get(0);
    Integer topicId = topic.getId();
    String topicName = topic.getTopicName();
    String email = topic.getCreatedBy();
    TopicRequestDto topicRequestDto = new TopicRequestDto(topicName, Set.of("tag1", "tag2"));

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
    when(topicRepository.existsByTopicName(topicName)).thenReturn(true);

    // When / Then
    assertThrows(ValueNotUniqException.class,
        () -> topicService.update(topicId, topicRequestDto, email));
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
  @DisplayName("addUniqTags should return an empty set when input tags are empty")
  public void addUniqTags_shouldReturnEmptySet_whenInputTagsEmpty() {
    // Given
    Set<String> emptyTags = Collections.emptySet();

    // When
    Set<Tag> result = topicService.addUniqTags(emptyTags);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("addUniqTags should return existing tags when all tags exist")
  public void addUniqTags_shouldReturnExistingTags_whenAllTagsExist() {
    // Given
    Set<String> tags = Set.of("tag1", "tag2", "tag3");

    Set<Tag> existingTags = tags.stream()
        .map(Tag::new)
        .collect(Collectors.toSet());

    when(tagRepository.findAllByNameIn(tags)).thenReturn(existingTags);

    // When
    Set<Tag> result = topicService.addUniqTags(tags);

    // Then
    assertEquals(existingTags, result);
  }

  @Test
  @DisplayName("addUniqTags should return new tags when some tags do not exist")
  public void addUniqTags_shouldReturnNewTags_whenSomeTagsNotExist() {
    // Given
    Set<String> savedTags = Set.of("tag1", "tag2");
    Set<String> inputTags = new HashSet<>(savedTags);
    inputTags.add("tag3");
    inputTags.add("tag4");

    Set<Tag> existingTags = savedTags.stream()
        .map(Tag::new)
        .collect(Collectors.toSet());

    Set<Tag> newTags = new HashSet<>();
    newTags.add(new Tag("tag3"));
    newTags.add(new Tag("tag4"));

    when(tagRepository.findAllByNameIn(inputTags)).thenReturn(existingTags);
    when(tagRepository.saveAll(anySet())).thenReturn(newTags.stream().toList());

    // When
    Set<Tag> result = topicService.addUniqTags(inputTags);

    // Then
    existingTags.addAll(newTags);

    assertEquals(existingTags, result);
  }

  @Test
  @DisplayName("addUniqTags should return new tags when all tags do not exist")
  public void addUniqTags_shouldReturnNewTags_whenAllTagsNotExist() {
    // Given
    Set<String> inputTags = Set.of("tag1", "tag2", "tag3", "tag4");

    Set<Tag> newTags = inputTags.stream()
        .map(Tag::new)
        .collect(Collectors.toSet());

    when(tagRepository.findAllByNameIn(inputTags)).thenReturn(new HashSet<>());
    when(tagRepository.saveAll(anySet())).thenReturn(newTags.stream().toList());

    // When
    Set<Tag> result = topicService.addUniqTags(inputTags);

    // Then
    assertEquals(newTags, result);
  }

  @Test
  @DisplayName("findById should throw TopicNotFoundException when topic does not exist")
  public void findById_shouldThrowTopicNotFoundException_whenTopicIsNotExist() {
    // Given
    when(topicRepository.findById(NOT_EXISTED_TOPIC_ID)).thenReturn(Optional.empty());

    // When and Then
    assertThrows(TopicNotFoundException.class, () -> topicService.findById(NOT_EXISTED_TOPIC_ID));

    Mockito.verify(topicRepository, Mockito.times(1)).findById(anyInt());
  }

  @Test
  @DisplayName("findAllPublic should return a list of TopicResponseDto")
  public void findAllPublic_shouldReturnListOfTopicResponseDto() {
    // Given
    List<Topic> topics = getTopics();

    when(topicRepository.findAllByIsPublicIsTrue()).thenReturn(topics);

    // When
    List<TopicResponseDto> topicResponseDtos = topicService.findAllPublic();

    // Then
    assertThat(topicResponseDtos).isNotNull();
    assertThat(topicResponseDtos).hasSize(2);
    assertTopicEquals(topics.get(0), topicResponseDtos.get(0));
    assertTopicEquals(topics.get(1), topicResponseDtos.get(1));
  }

  @Test
  @DisplayName("findAllPublic should return empty list of TopicResponseDto")
  public void findAllPublic_shouldReturnEmptyListOfTopicResponseDto() {
    // Given
    List<Topic> emptyList = new ArrayList<>();

    when(topicRepository.findAllByIsPublicIsTrue()).thenReturn(emptyList);

    // When
    List<TopicResponseDto> topicResponseDtos = topicService.findAllPublic();

    // Then
    assertThat(topicResponseDtos).isNotNull();
    assertThat(topicResponseDtos).isEmpty();
  }

  @Test
  @DisplayName("findTopicsByTag should return a list of topics when topics with the given tagId exist")
  public void findTopicsByTag_shouldReturnListOfTopics_whenTopicsWithGivenTagIdExist() {
    // Given
    List<Topic> topics = getTopics();
    String tagName = "#tag1";

    when(topicRepository.findAllByTagName(tagName)).thenReturn(topics);

    // When
    List<TopicResponseDto> result = topicService.findTopicsByTagName(tagName);

    // Then
    assertFalse(result.isEmpty());
    assertEquals(topics.size(), result.size());
  }

  @Test
  @DisplayName("deleteByCreator should delete topic by Id and Contact email")
  public void deleteByCreator_shouldDeleteTopicByIdAndContactEmail() {
    // Given
    Topic topic = getTopics().get(0);
    var id = topic.getId();
    var createdBy = topic.getCreatedBy();

    when(topicRepository.findById(id)).thenReturn(Optional.of(topic));

    // When
    topicService.deleteByCreator(id, createdBy);

    // Then
    verify(topicRepository, times(1)).delete(topic);
  }

  @Test
  @DisplayName("deleteByCreator should throw TopicAccessException")
  public void deleteByCreator_shouldThrowTopicAccessException() {
    // Given
    Topic topic = getTopics().get(1);
    var differentId = 1;
    var createdBy = topic.getCreatedBy();

    when(topicRepository.findById(differentId)).thenReturn(Optional.of(getTopics().get(0)));

    // When and Then
    assertThrows(TopicAccessException.class,
        () -> topicService.deleteByCreator(differentId, createdBy));

    Mockito.verify(topicRepository, Mockito.times(1)).findById(anyInt());
  }

  @Test
  @DisplayName("generatePrivateName should generate private name")
  public void generatePrivateName_shouldGeneratePrivateName() {
    // Given
    String sendTo = "vasil@gmail.com";
    String sentFrom = "anton@gmail.com";
    String expected = sentFrom + "-" + sendTo;

    // When
    String privateName = topicService.generatePrivateName(sendTo, sentFrom);

    // Then
    assertEquals(privateName, expected);
  }

  private List<Topic> getTopics() {
    Topic topic1 = Topic.builder()
        .id(1)
        .topicName("Topic")
        .createdBy("test-topic@gmail.com")
        .createdAt(LocalDateTime.now())
        .tags(getTags())
        .build();

    Topic topic2 = Topic.builder()
        .id(2)
        .topicName("Topic2")
        .createdBy("test-topic2@gmail.com")
        .createdAt(LocalDateTime.now())
        .tags(getTags())
        .build();

    return Arrays.asList(topic1, topic2);
  }

  private Set<Tag> getTags() {
    HashSet<Tag> tags = new HashSet<>();
    tags.add(new Tag(1, "#tag1"));
    tags.add(new Tag(2, "#tag2"));
    return tags;
  }

  private void assertTopicEquals(Topic topic, TopicResponseDto topicResponseDto) {
    List<String> responseTags = topicResponseDto.getTags().stream()
        .map(TagResponseDto::getName)
        .toList();

    List<String> requestTags = topic.getTags().stream()
        .map(Tag::getName)
        .toList();

    assertThat(topicResponseDto.getTopicName()).isEqualTo(topic.getTopicName());
    assertThat(topicResponseDto.getCreatedBy()).isEqualTo(topic.getCreatedBy());
    assertThat(topicResponseDto.getCreatedAt()).isEqualTo(topic.getCreatedAt());
    assertThat(topicResponseDto.getIsPublic()).isEqualTo(topic.getIsPublic());
    assertThat(responseTags).hasSameElementsAs(requestTags);
    assertThat(topicResponseDto.getTopicSubscribers()).isNull();
  }

}
