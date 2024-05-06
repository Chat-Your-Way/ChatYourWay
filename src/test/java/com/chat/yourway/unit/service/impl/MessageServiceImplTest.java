package com.chat.yourway.unit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.MessageMapperImpl;
import com.chat.yourway.mapper.TopicMapperImpl;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;
import com.chat.yourway.model.Topic;
import com.chat.yourway.repository.MessageRepository;
import com.chat.yourway.service.MessageServiceImpl;
import com.chat.yourway.service.interfaces.ContactService;
import com.chat.yourway.service.interfaces.TopicService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

  private static final String MAX_AMOUNT_REPORTS_FIELD_NAME = "maxAmountReports";
  private static final Byte MAX_AMOUNT_REPORTS_VALUE = 2;

  @Mock
  MessageRepository messageRepository;
  @Mock
  TopicService topicService;
  @Mock
  ContactService contactService;
  @Mock
  TopicSubscriberService topicSubscriberService;
  @InjectMocks
  MessageServiceImpl messageService;

  @BeforeEach
  void init() {
    TopicMapperImpl topicMapper = new TopicMapperImpl();
    ReflectionTestUtils.setField(messageService, "topicMapper", topicMapper);

    MessageMapperImpl messageMapper = new MessageMapperImpl();
    ReflectionTestUtils.setField(messageService, "messageMapper", messageMapper);
  }

  @Test
  @SneakyThrows
  @DisplayName("reportMessageById should save report to message when user makes report")
  public void reportMessageById_shouldSaveReportToMessage_WhenUserMakesReport() {
    // Given
    var messageId = 1;
    var email = "user@gmail.com";

    var field = MessageServiceImpl.class.getDeclaredField(MAX_AMOUNT_REPORTS_FIELD_NAME);
    field.setAccessible(true);
    field.set(messageService, MAX_AMOUNT_REPORTS_VALUE);

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.getCountReportsByMessageId(anyInt())).thenReturn(0);

    // When
    messageService.reportMessageById(messageId, email);

    // Then
    verify(messageRepository, never()).deleteById(anyInt());
    verify(messageRepository).saveReportFromContactToMessage(anyString(), anyInt());
  }

  @Test
  @SneakyThrows
  @DisplayName(
      "reportMessageById should delete message when user makes report and message reached max attempts")
  public void
  reportMessageById_shouldDeleteMessage_WhenUserMakesReportAndMessageReachedMaxAttempts() {
    // Given
    var messageId = 1;
    var email = "user@gmail.com";

    var field = MessageServiceImpl.class.getDeclaredField(MAX_AMOUNT_REPORTS_FIELD_NAME);
    field.setAccessible(true);
    field.set(messageService, MAX_AMOUNT_REPORTS_VALUE);

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.getCountReportsByMessageId(anyInt())).thenReturn(
        Integer.valueOf(MAX_AMOUNT_REPORTS_VALUE));

    // When
    messageService.reportMessageById(messageId, email);

    // Then
    verify(messageRepository).deleteById(anyInt());
    verify(messageRepository, never()).saveReportFromContactToMessage(anyString(), anyInt());
  }

  @Test
  @DisplayName(
      "reportMessageById should throw MessageHasAlreadyReportedException when user makes report again")
  public void reportMessageById_shouldThrowMessageHasAlreadyReportedException_WhenUserMakesReportAgain() {
    // Given
    var messageId = 1;
    var email = "user@gmail.com";

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.hasReportByContactEmailAndMessageId(anyString(), anyInt()))
        .thenReturn(true);

    // When
    // Then
    assertThrows(
        MessageHasAlreadyReportedException.class,
        () -> messageService.reportMessageById(messageId, email));

    verify(messageRepository, never()).deleteById(anyInt());
    verify(messageRepository, never()).saveReportFromContactToMessage(anyString(), anyInt());
  }

  @Test
  @DisplayName("createPublic should create public message")
  public void createPublic_shouldCreatePublicMessage() {
    // Given
    int topicId = 1;
    Message message = getPublicMessages().get(0);
    String sendTo = message.getSendTo();
    String sentFrom = message.getSentFrom();
    String content = message.getContent();
    MessagePublicRequestDto messageRequest = new MessagePublicRequestDto(content);

    TopicResponseDto topicResponseDto = new TopicResponseDto();
    topicResponseDto.setId(topicId);
    topicResponseDto.setTopicName(sendTo);

    when(topicService.findById(topicId)).thenReturn(topicResponseDto);
    when(messageRepository.save(any(Message.class))).thenReturn(message);
    when(topicSubscriberService.hasContactSubscribedToTopic(sentFrom, topicId)).thenReturn(true);
    when(contactService.findByEmail(anyString())).thenReturn(null);

    // When
    MessageResponseDto messageDto = messageService.createPublic(topicId, messageRequest, sentFrom);

    // Then
    assertMessageEquals(message, messageDto);
    verify(messageRepository, times(1)).save(any(Message.class));
  }

  @Test
  @DisplayName("createPublic should throw TopicNotFoundException when topic is not found")
  public void createPublic_shouldThrowTopicNotFoundException() {
    // Given
    int topicId = 99;
    String email = "vasil@gmail.com";
    MessagePublicRequestDto messageRequest = new MessagePublicRequestDto("Message content");

    when(topicService.findById(topicId)).thenThrow(new TopicNotFoundException("Topic not found"));

    // When and Then
    assertThrows(TopicNotFoundException.class,
        () -> messageService.createPublic(topicId, messageRequest, email));
  }

  @Test
  @DisplayName("createPublic should throw TopicSubscriberNotFoundException")
  public void createPublic_shouldThrowTopicSubscriberNotFoundException() {
    // Given
    int topicId = 1;
    String email = "vasil@gmail.com";
    MessagePublicRequestDto messageRequest = new MessagePublicRequestDto("Message content");

    when(topicService.findById(topicId)).thenReturn(new TopicResponseDto());
    when(topicSubscriberService.hasContactSubscribedToTopic(email, topicId)).thenReturn(false);

    // When and Then
    assertThrows(TopicSubscriberNotFoundException.class,
        () -> messageService.createPublic(topicId, messageRequest, email));
  }

  @Test
  @DisplayName("createPrivate should create private message")
  public void createPrivate_shouldCreatePrivateMessage() {
    // Given
    int topicId = 1;
    Message message = getPrivateMessages().get(0);
    String sendTo = message.getSendTo();
    String sentFrom = message.getSentFrom();
    String content = message.getContent();
    String topicName = sendTo + "<->" + sentFrom;
    MessagePrivateRequestDto messageRequest = new MessagePrivateRequestDto(sendTo, content);

    TopicResponseDto topicResponseDto = new TopicResponseDto();
    topicResponseDto.setId(topicId);
    topicResponseDto.setTopicName(topicName);

    when(topicService.generatePrivateName(sendTo, sentFrom)).thenReturn(topicName);
    when(topicService.findByName(topicName)).thenReturn(topicResponseDto);
    when(messageRepository.save(any(Message.class))).thenReturn(message);
    when(contactService.findByEmail(anyString())).thenReturn(null);

    // When
    MessageResponseDto messageDto = messageService.createPrivate(messageRequest, sentFrom);

    // Then
    assertMessageEquals(message, messageDto);
    verify(messageRepository, times(1)).save(any(Message.class));
  }

  @Test
  @DisplayName("createPrivate should throw TopicNotFoundException when topic not found")
  public void createPrivate_shouldThrowTopicNotFoundException() {
    // Given
    String sentFrom = "vasil@gmail.com";
    String sendTo = "anton@gmail.com";
    String topicName = "NonExistentTopicName";
    MessagePrivateRequestDto messageRequest = new MessagePrivateRequestDto(sendTo, "Message");

    when(topicService.generatePrivateName(anyString(), anyString())).thenReturn(topicName);
    when(topicService.findByName(topicName)).thenThrow(
        new TopicNotFoundException("Topic not found"));

    // When and Then
    assertThrows(TopicNotFoundException.class,
        () -> messageService.createPrivate(messageRequest, sentFrom));
  }

  @Test
  @DisplayName("findAllByTopicId should return list of messages")
  public void findAllByTopicId_shouldReturnListOfMessages() {
    // Given
    int topicId = 1;
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "timestamp");
    PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
    List<Message> messages = getPublicMessages();
    when(messageRepository.findAllByTopicId(topicId, pageable)).thenReturn(messages);

    // When
    List<MessageResponseDto> result = messageService.findAllByTopicId(topicId, pageRequestDto);

    // Then
    assertNotNull(result);
    assertEquals(messages.size(), result.size());
    for (int i = 0; i < messages.size(); i++) {
      assertMessageEquals(messages.get(i), result.get(i));
    }
    verify(messageRepository, times(1)).findAllByTopicId(topicId, pageable);
  }

  @Test
  @DisplayName("findAllByTopicId should return empty list")
  public void findAllByTopicId_shouldReturnEmptyList() {
    // Given
    int nonExistentTopicId = 99;
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "timestamp");
    PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
    when(messageRepository.findAllByTopicId(nonExistentTopicId, pageable)).thenReturn(List.of());

    // When
    List<MessageResponseDto> result = messageService.findAllByTopicId(nonExistentTopicId, pageRequestDto);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(messageRepository, times(1)).findAllByTopicId(nonExistentTopicId, pageable);
  }

  @Test
  @DisplayName("countMessagesBetweenTimestampByTopicId should return amount of messages")
  public void countMessagesBetweenTimestampByTopicId_shouldReturnAmountOfMessages() {
    // Given
    int topicId = 1;
    String sentFrom = "vasil@gmail.com";
    LocalDateTime timestamp = LocalDateTime.now();
    int countedMessages = 5;

    when(messageRepository.countMessagesBetweenTimestampByTopicId(
        eq(topicId),
        eq(sentFrom),
        eq(timestamp),
        any(LocalDateTime.class)))
        .thenReturn(countedMessages);

    // When
    int result = messageService.countMessagesBetweenTimestampByTopicId(topicId, sentFrom, timestamp);

    // Then
    assertEquals(countedMessages, result, "Should return the expected count");

    // Verify
    verify(messageRepository, times(1))
        .countMessagesBetweenTimestampByTopicId(
            eq(topicId),
            eq(sentFrom),
            eq(timestamp),
            any(LocalDateTime.class));
  }

  //-----------------------------------
  //         Private methods
  //-----------------------------------

  private List<Message> getPrivateMessages() {
    return getMessages("anton@gmail.com", "vasil@gmail.com");
  }

  private List<Message> getPublicMessages() {
    return getMessages("Topic id=1", "Topic id=2");
  }

  private List<Message> getMessages(String... listOfSendTo) {
    Message message1 = Message.builder()
        .id(1)
        .sentFrom("vasil@gmail.com")
        .sendTo(listOfSendTo[0])
        .timestamp(LocalDateTime.now())
        .topic(getTopics().get(0))
        .build();

    Message message2 = Message.builder()
        .id(2)
        .sentFrom("anton@gmail.com")
        .sendTo(listOfSendTo[1])
        .timestamp(LocalDateTime.now())
        .topic(getTopics().get(0))
        .build();

    return List.of(message1, message2);
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

    return List.of(topic1, topic2);
  }

  private void assertMessageEquals(Message message, MessageResponseDto messageResponseDto) {
    assertNotNull(messageResponseDto);
    assertThat(messageResponseDto.getSentFrom()).isEqualTo(message.getSentFrom());
    assertThat(messageResponseDto.getSendTo()).isEqualTo(message.getSendTo());
    assertThat(messageResponseDto.getTimestamp()).isEqualTo(message.getTimestamp());
    assertThat(messageResponseDto.getContent()).isEqualTo(message.getContent());
  }

}
