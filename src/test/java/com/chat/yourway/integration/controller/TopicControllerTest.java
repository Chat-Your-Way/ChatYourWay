package com.chat.yourway.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chat.yourway.dto.request.TopicPrivateRequestDto;
import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.TagResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.model.Tag;
import com.chat.yourway.model.Topic;
import com.chat.yourway.model.TopicSubscriber;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.repository.TagRepository;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.interfaces.TopicService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class TopicControllerTest {

  @Autowired
  private TopicService topicService;
  @Autowired
  private TopicRepository topicRepository;
  @Autowired
  private TopicSubscriberRepository topicSubscriberRepository;
  @Autowired
  private ContactRepository contactRepository;
  @Autowired
  private TopicSubscriberService topicSubscriberService;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  private static final String URI = "/topics";
  private final Integer NOT_EXISTED_TOPIC_ID = 99;

  @AfterEach
  public void cleanup() {
    topicRepository.deleteAll();
    topicSubscriberRepository.deleteAll();
    contactRepository.deleteAll();
    tagRepository.deleteAll();
  }

  //-----------------------------------
  //               POST
  //-----------------------------------

  @Test
  @DisplayName("create should create a new topic")
  public void create_shouldCreateNewTopic() throws Exception {
    // Given
    Topic newTopic = getTopics().get(1);
    TopicRequestDto topicRequestDto = new TopicRequestDto();
    topicRequestDto.setTopicName(newTopic.getTopicName());
    topicRequestDto.setTags(new HashSet<>(getTags()));

    mockMvc.perform(post(URI + "/create")
            .content(objectMapper.writeValueAsString(topicRequestDto))
            .principal(new TestingAuthenticationToken(newTopic.getCreatedBy(), null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.topicName").value(newTopic.getTopicName()))
        .andExpect(jsonPath("$.createdBy").value(newTopic.getCreatedBy()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.isPublic").value(true))
        .andExpect(jsonPath("$.tags").isNotEmpty())
        .andExpect(jsonPath("$.topicSubscribers").doesNotExist());
  }

  @Test
  @DisplayName("createPrivate should create a new private topic")
  public void createPrivate_shouldCreateNewPrivateTopic() throws Exception {
    // Given
    String sentFrom = "vasil@gmail.com";
    Contact recipient = getContacts().get(0);
    String sendTo = recipient.getEmail();
    TopicPrivateRequestDto topicRequestDto = new TopicPrivateRequestDto(sendTo);

    contactRepository.save(recipient);

    mockMvc.perform(post(URI + "/create/private")
            .content(objectMapper.writeValueAsString(topicRequestDto))
            .principal(new TestingAuthenticationToken(sentFrom, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.topicName").value(sendTo + "-" + sentFrom))
        .andExpect(jsonPath("$.createdBy").value(sentFrom))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.isPublic").value(false))
        .andExpect(jsonPath("$.tags").isEmpty())
        .andExpect(jsonPath("$.topicSubscribers").doesNotExist());
  }

  @Test
  @DisplayName("create should return ValueNotUniqException")
  public void create_shouldReturnValueNotUniqException() throws Exception {
    // Given
    Topic existingTopic = topicRepository.save(getTopics().get(0));
    String duplicateTopicName = existingTopic.getTopicName();

    TopicRequestDto topicRequestDto = new TopicRequestDto();
    topicRequestDto.setTopicName(duplicateTopicName);
    topicRequestDto.setTags(new HashSet<>(getTags()));

    mockMvc.perform(post(URI + "/create")
            .content(objectMapper.writeValueAsString(topicRequestDto))
            .principal(new TestingAuthenticationToken(existingTopic.getCreatedBy(), null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(result ->
            assertThat(result.getResolvedException()).isInstanceOf(ValueNotUniqException.class));
  }

  @Test
  @DisplayName("create_should create topic with new tags successfully")
  public void create_shouldCreateTopicWithNewTagsSuccessfully() throws Exception {
    // Given
    TopicRequestDto topicRequestDto = new TopicRequestDto();
    topicRequestDto.setTopicName("Updated Topic");
    topicRequestDto.setTags(new HashSet<>(getTags()));

    mockMvc.perform(post(URI + "/create")
            .content(objectMapper.writeValueAsString(topicRequestDto))
            .principal(new TestingAuthenticationToken("user@gmail.com", null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.topicName").value(topicRequestDto.getTopicName()))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.size()").value(2));
  }

  @Test
  @DisplayName("update should update topic successfully")
  public void update_shouldUpdateTopicSuccessfully() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();
    String updatedTopicName = "Updated Topic";
    String userEmail = savedTopic.getCreatedBy();

    TopicRequestDto updatedTopicRequestDto = new TopicRequestDto();
    updatedTopicRequestDto.setTopicName(updatedTopicName);
    updatedTopicRequestDto.setTags(new HashSet<>());

    mockMvc.perform(put(URI + "/update/{id}", topicId)
            .content(objectMapper.writeValueAsString(updatedTopicRequestDto))
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(topicId))
        .andExpect(jsonPath("$.topicName").value(updatedTopicName))
        .andExpect(jsonPath("$.createdBy").value(userEmail))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.tags").isEmpty())
        .andExpect(jsonPath("$.topicSubscribers").isEmpty());

    // Verify
    Optional<Topic> updatedTopic = topicRepository.findById(topicId);
    assertThat(updatedTopic).isPresent();
    assertThat(updatedTopic.get().getTopicName()).isEqualTo(updatedTopicName);
  }

  @Test
  @DisplayName("update should return TopicAccessException when unauthorized to update")
  public void update_shouldReturnTopicAccessExceptionWhenUnauthorizedToUpdate() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();
    String unauthorizedUserEmail = "unauthorizedUser@gmail.com";

    TopicRequestDto updatedTopicRequestDto = new TopicRequestDto();
    updatedTopicRequestDto.setTopicName("Updated Topic");
    updatedTopicRequestDto.setTags(new HashSet<>());

    mockMvc.perform(put(URI + "/update/{id}", topicId)
            .content(objectMapper.writeValueAsString(updatedTopicRequestDto))
            .principal(new TestingAuthenticationToken(unauthorizedUserEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(result ->
            assertThat(result.getResolvedException()).isInstanceOf(TopicAccessException.class));
  }

  @Test
  @DisplayName("update_should return TopicNotFoundException when topic does not exist")
  public void update_shouldReturnTopicNotFoundExceptionWhenTopicDoesNotExist() throws Exception {
    mockMvc.perform(get(URI + "/{id}", NOT_EXISTED_TOPIC_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            TopicNotFoundException.class));
  }

  @Test
  @DisplayName("update_should return ValueNotUniqException when updating with duplicate topic name")
  public void update_shouldReturnValueNotUniqExceptionWhenUpdatingWithDuplicateTopicName()
      throws Exception {
    // Given
    Topic savedTopic1 = topicRepository.save(getTopics().get(0));
    Topic savedTopic2 = topicRepository.save(getTopics().get(1));
    Integer topic2Id = savedTopic2.getId();
    String savedTopic2Email = savedTopic2.getCreatedBy();

    TopicRequestDto updatedTopicRequestDto = new TopicRequestDto();
    updatedTopicRequestDto.setTopicName(savedTopic1.getTopicName());

    mockMvc.perform(put(URI + "/update/{id}", topic2Id)
            .content(objectMapper.writeValueAsString(updatedTopicRequestDto))
            .principal(new TestingAuthenticationToken(savedTopic2Email, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            ValueNotUniqException.class));
  }

  @Test
  @DisplayName("update_should update topic with new tags successfully")
  @Transactional
  public void update_shouldUpdateTopicWithNewTagsSuccessfully() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();
    String userEmail = savedTopic.getCreatedBy();

    TopicRequestDto updatedTopicRequestDto = new TopicRequestDto();
    updatedTopicRequestDto.setTopicName("Updated Topic");
    updatedTopicRequestDto.setTags(new HashSet<>(getTags()));

    mockMvc.perform(put(URI + "/update/{id}", topicId)
            .content(objectMapper.writeValueAsString(updatedTopicRequestDto))
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.topicName").value(updatedTopicRequestDto.getTopicName()));

    TopicResponseDto topicResponseDto = topicService.findById(topicId);
    Set<TagResponseDto> updatedTags = topicResponseDto.getTags();

    assertThat(updatedTags).isNotEmpty();
    assertThat(updatedTags).extracting(TagResponseDto::getName).containsAll(getTags());
  }

  @Test
  @DisplayName("should subscribe to the topic successfully")
  public void subscribe_shouldSubscribeToTopicSuccessfully() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Contact savedContact = contactRepository.save(getContacts().get(0));
    Integer topicId = savedTopic.getId();
    String userEmail = savedContact.getEmail();

    mockMvc.perform(post(URI + "/subscribe/{topicId}", topicId)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
    assertThat(
        topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(userEmail,
            topicId)).isTrue();
  }

  @Test
  @DisplayName("subscribe should wasn't subscribed")
  public void subscribe_shouldWasNotSubscribed() throws Exception {
    // Given
    Contact savedContact = contactRepository.save(getContacts().get(0));
    String userEmail = savedContact.getEmail();

    mockMvc.perform(post(URI + "/subscribe/{topicId}", NOT_EXISTED_TOPIC_ID)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
    assertThat(
        topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(userEmail,
            NOT_EXISTED_TOPIC_ID)).isFalse();
  }

  @Test
  @DisplayName("subscribe should return ContactAlreadySubscribedToTopicException")
  public void subscribe_shouldReturnContactAlreadySubscribedToTopicException() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Contact savedContact = contactRepository.save(getContacts().get(0));
    Integer topicId = savedTopic.getId();
    String userEmail = savedContact.getEmail();
    topicSubscriberRepository.save(TopicSubscriber.builder()
        .contact(savedContact)
        .topic(savedTopic)
        .subscribeAt(LocalDateTime.now())
        .build());

    mockMvc.perform(post(URI + "/subscribe/{topicId}", topicId)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            ContactAlreadySubscribedToTopicException.class));

  }

  //-----------------------------------
  //               PATCH
  //-----------------------------------

  @Test
  @DisplayName("unsubscribe should unsubscribe from the topic successfully")
  public void unsubscribe_shouldUnsubscribeFromTopicSuccessfully() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Contact savedContact = contactRepository.save(getContacts().get(0));
    Integer topicId = savedTopic.getId();
    String userEmail = savedContact.getEmail();
    topicSubscriberService.subscribeToTopicById(userEmail, topicId);

    mockMvc.perform(patch(URI + "/unsubscribe/{topicId}", topicId)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
    assertThat(
        topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(userEmail,
            topicId)).isFalse();
  }

  @Test
  @DisplayName("unsubscribe should return TopicSubscriberNotFoundException")
  public void unsubscribe_shouldReturnTopicSubscriberNotFoundException() throws Exception {
    // Given
    Contact savedContact = contactRepository.save(getContacts().get(0));
    String userEmail = savedContact.getEmail();

    mockMvc.perform(patch(URI + "/unsubscribe/{topicId}", NOT_EXISTED_TOPIC_ID)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            TopicSubscriberNotFoundException.class));
  }

  //-----------------------------------
  //               GET
  //-----------------------------------

  @Test
  @DisplayName("findTopicById should return topic by topicId")
  public void findTopicById_shouldReturnTopicById() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();

    mockMvc.perform(get(URI + "/{id}", topicId).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.topicName").value(savedTopic.getTopicName()))
        .andExpect(jsonPath("$.createdBy").value(savedTopic.getCreatedBy()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.tags").isEmpty())
        .andExpect(jsonPath("$.topicSubscribers").isEmpty());
  }

  @Test
  @DisplayName("findTopicById should return TopicNotFoundException")
  public void findTopicById_shouldReturnTopicNotFoundException() throws Exception {

    mockMvc.perform(get(URI + "/{id}", NOT_EXISTED_TOPIC_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            TopicNotFoundException.class));
  }

  @Test
  @DisplayName("findAllPublic should return empty list of all public topics")
  void findAllPublic_shouldReturnEmptyListOfAllPublicTopics() throws Exception {
    // Given
    topicRepository.deleteAll();

    mockMvc.perform(get(URI + "/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("findAllPublic should return list of all public topics")
  public void findAllPublic_shouldReturnListOfAllPublicTopics() throws Exception {
    // Given
    List<Topic> savedTopics = topicRepository.saveAll(getTopics());

    mockMvc.perform(get(URI + "/all").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[1].id").isNumber())
        .andExpect(jsonPath("$[0].topicName").value(savedTopics.get(0).getTopicName()))
        .andExpect(jsonPath("$[0].createdBy").value(savedTopics.get(0).getCreatedBy()))
        .andExpect(jsonPath("$[1].topicName").value(savedTopics.get(1).getTopicName()))
        .andExpect(jsonPath("$[1].createdBy").value(savedTopics.get(1).getCreatedBy()))
        .andExpect(jsonPath("$[*].createdAt").isNotEmpty())
        .andExpect(jsonPath("$[0].isPublic").value(true))
        .andExpect(jsonPath("$[1].isPublic").value(true))
        .andExpect(jsonPath("$[*].tags").isArray())
        .andExpect(jsonPath("$[*].topicSubscribers").isArray());
  }

  @Test
  @DisplayName("findAllSubscribers should find all subscribers to topic by topicId")
  public void findAllSubscribers_shouldFindAllSubscribersByTopicId() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();
    List<Contact> savedContacts = contactRepository.saveAll(getContacts());
    String email1 = savedContacts.get(0).getEmail();
    String email2 = savedContacts.get(1).getEmail();
    topicSubscriberService.subscribeToTopicById(email1, topicId);
    topicSubscriberService.subscribeToTopicById(email2, topicId);

    mockMvc.perform(get(URI + "/subscribers/{topicId}", topicId)
            .principal(new TestingAuthenticationToken(email1, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[0].nickname").value(savedContacts.get(0).getNickname()))
        .andExpect(jsonPath("$[0].email").value(savedContacts.get(0).getEmail()))
        .andExpect(jsonPath("$[0].avatarId").isNumber())
        .andExpect(jsonPath("$[1].id").isNumber())
        .andExpect(jsonPath("$[1].nickname").value(savedContacts.get(1).getNickname()))
        .andExpect(jsonPath("$[1].email").value(savedContacts.get(1).getEmail()))
        .andExpect(jsonPath("$[1].avatarId").isNumber());
  }

  @Test
  @DisplayName("findAllByTagByName should return list of topics with expected size when user chose by tag name")
  public void findAllByTagByName_shouldReturnListOfTopicsWithExpectedSize_WhenUserChoseByTagId()
      throws Exception {
    // Given
    Tag tag = tagRepository.save(new Tag("tag1"));
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);
    Topic topic = getTopics().get(0);
    topic.setTags(tags);
    topicRepository.save(topic);

    mockMvc.perform(get(URI + "/all/{tag}", tag.getName())
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  @DisplayName("findAllByTopicName should return empty list of all topics by name")
  void findAllByTopicName_shouldReturnEmptyListOfAllTopicsByName() throws Exception {
    // Given
    topicRepository.deleteAll();

    mockMvc.perform(get(URI + "/search")
            .param("topicName", "NotExistsName"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("findAllByTopicName should return list of all topics by name")
  public void findAllByTopicName_shouldReturnListOfAllTopicsByName() throws Exception {
    // Given
    List<Topic> savedTopics = topicRepository.saveAll(getTopics());

    mockMvc.perform(get(URI + "/search")
            .param("topicName", "topic")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[1].id").isNumber())
        .andExpect(jsonPath("$[0].topicName").value(savedTopics.get(0).getTopicName()))
        .andExpect(jsonPath("$[0].createdBy").value(savedTopics.get(0).getCreatedBy()))
        .andExpect(jsonPath("$[1].topicName").value(savedTopics.get(1).getTopicName()))
        .andExpect(jsonPath("$[1].createdBy").value(savedTopics.get(1).getCreatedBy()))
        .andExpect(jsonPath("$[*].createdAt").isNotEmpty())
        .andExpect(jsonPath("$[0].isPublic").value(true))
        .andExpect(jsonPath("$[1].isPublic").value(true))
        .andExpect(jsonPath("$[*].tags").isArray())
        .andExpect(jsonPath("$[*].topicSubscribers").isArray());
  }

  //-----------------------------------
  //               DELETE
  //-----------------------------------

  @Test
  @DisplayName("deleteById should delete topic by id and creator successfully")
  public void deleteById_shouldDeleteTopicByIdAndCreatorSuccessfully() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();
    String userEmail = savedTopic.getCreatedBy();

    mockMvc.perform(delete(URI + "/{id}", topicId)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
    assertThat(topicRepository.findById(topicId)).isNotPresent();
  }

  @Test
  @DisplayName("deleteById should return TopicAccessException")
  public void deleteById_shouldReturnTopicAccessException() throws Exception {
    // Given
    Topic savedTopic = topicRepository.save(getTopics().get(0));
    Integer topicId = savedTopic.getId();
    String userEmail = "newuser@gmail.com";

    mockMvc.perform(delete(URI + "/{id}", topicId)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            TopicAccessException.class));
  }

  //-----------------------------------
  //         Private methods
  //-----------------------------------

  private List<String> getTags() {
    return List.of("#tag1", "#tag2");
  }

  private List<Topic> getTopics() {
    Topic topic1 = Topic.builder()
        .topicName("new Topic 1")
        .isPublic(true)
        .createdBy("user1@gmail.com")
        .createdAt(LocalDateTime.parse("2023-09-18T22:38:29.65851"))
        .tags(new HashSet<>())
        .topicSubscribers(new HashSet<>())
        .build();

    Topic topic2 = Topic.builder()
        .topicName("new Topic 2")
        .isPublic(true)
        .createdBy("user2@gmail.com")
        .createdAt(LocalDateTime.parse("2023-09-18T23:30:29.65851"))
        .tags(new HashSet<>())
        .topicSubscribers(new HashSet<>())
        .build();

    return Arrays.asList(topic1, topic2);
  }

  private List<Contact> getContacts() {
    Contact contact1 = Contact.builder()
        .nickname("nickname")
        .avatarId((byte) 1)
        .email("contact@gmail.com")
        .isActive(true)
        .isPrivate(true)
        .password("123456789")
        .role(Role.USER)
        .build();

    Contact contact2 = Contact.builder()
        .nickname("nickname2")
        .avatarId((byte) 2)
        .email("contact2@gmail.com")
        .isActive(true)
        .isPrivate(true)
        .password("0000000")
        .role(Role.USER)
        .build();
    return Arrays.asList(contact1, contact2);
  }

}