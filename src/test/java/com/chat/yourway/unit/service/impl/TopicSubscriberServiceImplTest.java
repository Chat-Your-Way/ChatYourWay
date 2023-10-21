package com.chat.yourway.unit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.mapper.ContactMapperImpl;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.TopicSubscriberServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TopicSubscriberServiceImplTest {

  @Mock
  private TopicSubscriberRepository topicSubscriberRepository;
  @InjectMocks
  TopicSubscriberServiceImpl topicSubscriberService;

  private static final String EMAIL = "test@example.com";
  private static final Integer TOPIC_ID = 1;

  @BeforeEach
  void init() {
    ContactMapperImpl contactMapper = new ContactMapperImpl();
    ReflectionTestUtils.setField(topicSubscriberService, "contactMapper", contactMapper);
  }

  @Test
  @DisplayName("subscribeToTopicById should subscribe contact to the topic")
  public void subscribeToTopicById_shouldSubscribeContactToTopic() {
    // Given
    when(topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(EMAIL,
        TOPIC_ID))
        .thenReturn(false);

    // When
    topicSubscriberService.subscribeToTopicById(EMAIL, TOPIC_ID);

    // Then
    verify(topicSubscriberRepository, times(1)).subscribe(EMAIL, TOPIC_ID);
  }

  @Test
  @DisplayName("subscribeToTopicById should throw ContactAlreadySubscribedToTopicException if contact is already subscribed")
  public void subscribeToTopicById_shouldThrowExceptionIfContactAlreadySubscribed() {
    // Given
    when(topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(EMAIL,
        TOPIC_ID))
        .thenReturn(true);

    // When and Then
    assertThrows(ContactAlreadySubscribedToTopicException.class,
        () -> topicSubscriberService.subscribeToTopicById(EMAIL, TOPIC_ID));
  }

  @Test
  @DisplayName("unsubscribeFromTopicById should unsubscribe contact from the topic")
  public void unsubscribeFromTopicById_shouldUnsubscribeContactFromTopic() {
    // Given
    when(topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(EMAIL,
        TOPIC_ID))
        .thenReturn(true);

    // When
    topicSubscriberService.unsubscribeFromTopicById(EMAIL, TOPIC_ID);

    // Then
    verify(topicSubscriberRepository, times(1)).unsubscribe(EMAIL, TOPIC_ID);
  }

  @Test
  @DisplayName("unsubscribeFromTopicById should throw TopicSubscriberNotFoundException if contact is not subscribed")
  public void unsubscribeFromTopicById_shouldThrowExceptionIfContactNotSubscribed() {
    // Given
    when(topicSubscriberRepository.existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(EMAIL,
        TOPIC_ID))
        .thenReturn(false);

    // When and Then
    assertThrows(TopicSubscriberNotFoundException.class,
        () -> topicSubscriberService.unsubscribeFromTopicById(EMAIL, TOPIC_ID));
  }

  @Test
  @DisplayName("findAllSubscribersByTopicId should return a list of ContactResponseDto")
  public void findAllSubscribersByTopicId_shouldReturnListOfContactResponseDto() {
    // Given
    List<Contact> subscribers = getContacts();

    when(topicSubscriberRepository.findAllActiveSubscribersByTopicId(TOPIC_ID))
        .thenReturn(subscribers);

    // When
    List<ContactResponseDto> subscribersDto = topicSubscriberService.findAllSubscribersByTopicId(
        TOPIC_ID);

    // Then
    assertThat(subscribersDto).isNotNull();
    assertThat(subscribersDto).hasSize(2);
    assertContactEquals(subscribers.get(0), subscribersDto.get(0));
    assertContactEquals(subscribers.get(1), subscribersDto.get(1));
  }

  @Test
  @DisplayName("findAllSubscribersByTopicId should return empty list of ContactResponseDto")
  public void findAllSubscribersByTopicId_shouldReturnEmptyListOfContactResponseDto() {
    // Given
    List<Contact> subscribers = new ArrayList<>();

    when(topicSubscriberRepository.findAllActiveSubscribersByTopicId(TOPIC_ID))
        .thenReturn(subscribers);

    // When
    List<ContactResponseDto> subscribersDto = topicSubscriberService.findAllSubscribersByTopicId(
        TOPIC_ID);

    // Then
    assertThat(subscribersDto).isNotNull();
    assertThat(subscribersDto).isEmpty();
  }

  private List<Contact> getContacts() {
    Contact contact1 = Contact.builder()
        .id(1)
        .nickname("nickname1")
        .email("test1@gmail.com")
        .avatarId((byte) 1)
        .isPrivate(true)
        .isActive(true)
        .build();

    Contact contact2 = Contact.builder()
        .id(2)
        .nickname("nickname2")
        .email("test2@gmail.com")
        .avatarId((byte) 2)
        .isPrivate(true)
        .isActive(true)
        .build();

    return Arrays.asList(contact1, contact2);
  }

  private void assertContactEquals(Contact contact, ContactResponseDto contactResponseDto) {
    assertThat(contactResponseDto.getId()).isEqualTo(contact.getId());
    assertThat(contactResponseDto.getNickname()).isEqualTo(contact.getNickname());
    assertThat(contactResponseDto.getEmail()).isEqualTo(contact.getEmail());
    assertThat(contactResponseDto.getAvatarId()).isEqualTo(contact.getAvatarId());
    assertThat(contactResponseDto.getIsPrivate()).isEqualTo(contact.getIsPrivate());
    assertThat(contactResponseDto.getIsActive()).isEqualTo(contact.getIsActive());
  }

}
