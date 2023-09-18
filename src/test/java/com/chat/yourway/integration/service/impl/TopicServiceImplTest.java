package com.chat.yourway.integration.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Role;
import com.chat.yourway.model.Topic;
import com.chat.yourway.model.TopicSubscriber;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class TopicServiceImplTest {

  @Autowired
  private TopicRepository topicRepository;
  @Autowired
  private TopicSubscriberRepository topicSubscriberRepository;
  @Autowired
  private ContactRepository contactRepository;
  @Autowired
  private TopicSubscriberService topicSubscriberService;
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
  }

  //-----------------------------------
  //               POST
  //-----------------------------------

  @Test
  @DisplayName("should create a new topic")
  public void shouldCreateNewTopic() throws Exception {
    // Given
    Topic newTopic = getTopics().get(1);

    mockMvc.perform(post(URI + "/create")
            .param("topicName", newTopic.getTopicName())
            .principal(new TestingAuthenticationToken(newTopic.getCreatedBy(), null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.topicName").value(newTopic.getTopicName()))
        .andExpect(jsonPath("$.createdBy").value(newTopic.getCreatedBy()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.topicSubscribers").doesNotExist());
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
  @DisplayName("should wasn't subscribed")
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
  @DisplayName("should return ContactAlreadySubscribedToTopicException")
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
        .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
            .equals(ContactAlreadySubscribedToTopicException.class));
  }

  //-----------------------------------
  //               PATCH
  //-----------------------------------

  @Test
  @DisplayName("should unsubscribe from the topic successfully")
  public void shouldUnsubscribeFromTopicSuccessfully() throws Exception {
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
  @DisplayName("should return TopicSubscriberNotFoundException")
  public void shouldReturnTopicSubscriberNotFoundException() throws Exception {
    // Given
    Contact savedContact = contactRepository.save(getContacts().get(0));
    String userEmail = savedContact.getEmail();

    mockMvc.perform(patch(URI + "/unsubscribe/{topicId}", NOT_EXISTED_TOPIC_ID)
            .principal(new TestingAuthenticationToken(userEmail, null))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
            .equals(TopicSubscriberNotFoundException.class));
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
        .andExpect(content().json(objectMapper.writeValueAsString(savedTopic)));
  }

  @Test
  @DisplayName("findTopicById should return TopicNotFoundException")
  public void findTopicById_shouldReturnTopicNotFoundException() throws Exception {

    mockMvc.perform(get(URI + "/{id}", NOT_EXISTED_TOPIC_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
            .equals(TopicNotFoundException.class));
  }

  @Test
  @DisplayName("should return empty list of all topics")
  void shouldReturnEmptyListOfAllTopics() throws Exception {
    // Given
    topicRepository.deleteAll();

    mockMvc.perform(get(URI))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("should return list of all topics")
  public void shouldReturnListOfAllTopics() throws Exception {
    // Given
    List<Topic> savedTopics = topicRepository.saveAll(getTopics());

    mockMvc.perform(get(URI).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(savedTopics)));
  }

  @Test
  @DisplayName("should find all subscribers to topic by topicId")
  public void shouldFindAllSubscribersByTopicId() throws Exception {
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
        .andExpect(content().json(objectMapper.writeValueAsString(savedContacts)));
  }

  //-----------------------------------
  //               DELETE
  //-----------------------------------

  @Test
  @DisplayName("should delete topic by creator successfully")
  public void shouldDeleteTopicByCreatorSuccessfully() throws Exception {
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
        .andExpect(status().isConflict())
        .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
            .equals(TopicAccessException.class));
  }

  //-----------------------------------
  //         Private methods
  //-----------------------------------

  private List<Topic> getTopics() {
    Topic topic1 = Topic.builder()
        .topicName("Topic1")
        .createdBy("user1@gmail.com")
        .createdAt(LocalDateTime.parse("2023-09-18T22:38:29.65851"))
        .topicSubscribers(new HashSet<>())
        .build();

    Topic topic2 = Topic.builder()
        .topicName("Topic2")
        .createdBy("user2@gmail.com")
        .createdAt(LocalDateTime.parse("2023-09-18T23:30:29.65851"))
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
