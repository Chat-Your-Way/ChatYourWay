package com.chat.yourway.integration.service.impl;

import com.chat.yourway.exception.NotSubscribedTopicException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.service.interfaces.ContactService;
import com.chat.yourway.service.interfaces.TopicService;
import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.listener.StompConnectionListener;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest
@TestExecutionListeners(
    value = {
      TransactionalTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DbUnitTestExecutionListener.class,
      MockitoTestExecutionListener.class,
      ResetMocksTestExecutionListener.class
    })
public class TopicSubscriberServiceImplTest {
  @Autowired TopicService topicService;
  @Autowired ContactService contactService;
  @Autowired TopicSubscriberService topicSubscriberService;

  static final String USER_EMAIL = "anton@gmail.com";

  @Autowired
  TopicSubscriberService topicSubscriberService;
  @Autowired
  StompConnectionListener stompConnectionListener;


  @Test
  @DatabaseSetup(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName("should successfully add topic to favourite when user mark topic as favourite")
  public void shouldSuccessfullyAddTopicToFavourite_whenUserMarkTopicAsFavourite() {
    var contactEmail = "vasil1@gmail.com";
    var contact = contactService.findByEmail(contactEmail);
    var expectedSize = 2;
    var topicId = 112;

    // When
    topicSubscriberService.addTopicToFavourite(topicId, contact);
    var resultList = topicService.findAllFavouriteTopics(contact);

    // Then
    assertThat(resultList.size())
        .withFailMessage("Expecting size of list of topics equals to " + expectedSize)
        .isEqualTo(expectedSize);
  }

  @Test
  @DatabaseSetup(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName(
      "should successfully remove topic from favourite when user unmark topic as favourite")
  public void shouldSuccessfullyRemoveTopicFromFavourite_whenUserUnmarkTopicAsFavourite() {
    var contactEmail = "vasil1@gmail.com";
    var contact = contactService.findByEmail(contactEmail);
    var expectedSize = 0;
    var topicId = 111;

    // When
    topicSubscriberService.removeTopicFromFavourite(topicId, contact);
    var resultList = topicService.findAllFavouriteTopics(contact);

    // Then
    assertThat(resultList.size())
        .withFailMessage("Expecting size of list of topics equals to " + expectedSize)
        .isEqualTo(expectedSize);
  }

  @Test
  @DatabaseSetup(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName(
      "should throw TopicNotFoundException when user mark topic as favourite and topic does not exist")
  public void shouldThrowTopicNotFoundException_whenUserMarkTopicAsFavouriteAndTopicDoesNotExist() {
    var contactEmail = "vasil1@gmail.com";
    var contact = contactService.findByEmail(contactEmail);
    var topicId = 1;

    // When
    // Then
    assertThrows(
        TopicNotFoundException.class,
        () -> topicSubscriberService.addTopicToFavourite(topicId, contact));
  }

  @Test
  @DatabaseSetup(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName(
      "should throw TopicNotFoundException when user unmark topic as favourite and topic does not exist")
  public void
      shouldThrowTopicNotFoundException_whenUserUnmarkTopicAsFavouriteAndTopicDoesNotExist() {
    var contactEmail = "vasil1@gmail.com";
    var contact = contactService.findByEmail(contactEmail);
    var topicId = 1;

    // When
    // Then
    assertThrows(
        TopicNotFoundException.class,
        () -> topicSubscriberService.removeTopicFromFavourite(topicId, contact));
  }

  @Test
  @DatabaseSetup(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName(
      "should throw NotSubscribedTopicException when user mark topic as favourite and user did not subscribe to topic")
  public void
      shouldThrowNotSubscribedTopicException_whenUserMarkTopicAsFavouriteAndUserDidNotSubscribeToTopic() {
    var contactEmail = "vasil1@gmail.com";
    var contact = contactService.findByEmail(contactEmail);
    var topicId = 113;

    // When
    // Then
    assertThrows(
        NotSubscribedTopicException.class,
        () -> topicSubscriberService.addTopicToFavourite(topicId, contact));
  }

  @Test
  @DatabaseSetup(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/favourite-topics-of-contact.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName(
      "should throw NotSubscribedTopicException when user unmark topic as favourite and user did not subscribe to topic")
  public void
      shouldThrowNotSubscribedTopicException_whenUserUnmarkTopicAsFavouriteAndUserDidNotSubscribeToTopic() {
    var contactEmail = "vasil1@gmail.com";
    var contact = contactService.findByEmail(contactEmail);
    var topicId = 113;

    // When
    // Then
    assertThrows(
        NotSubscribedTopicException.class,
        () -> topicSubscriberService.removeTopicFromFavourite(topicId, contact));
  }

    @Test
    @DisplayName("should return lust subscribers who online in topic when user made request")
    @DatabaseSetup(
            value = "/dataset/get-subscribers-from-topic.xml",
            type = DatabaseOperation.INSERT)
    @DatabaseTearDown(
            value = "/dataset/get-subscribers-from-topic.xml",
            type = DatabaseOperation.DELETE)
    void shouldReturnListSubscribersWhoOnlineInTopic_whenUserMadeRequest() {
        // Given
        var topicId = 1;
        var expectedSize = 1;

        // When
        connectUser();
        var response = topicSubscriberService.findAllOnlineContactsByTopicId(topicId);
        disconnectUser();

        // Then
        assertThat(response.size())
                .withFailMessage("Expecting size of list of online subs equals to " + expectedSize)
                .isEqualTo(expectedSize);
        assertThat(response.get(0)).extracting(ContactResponseDto::getEmail).isEqualTo(USER_EMAIL);
    }

    private void connectUser() {
        stompConnectionListener.handleWebSocketConnectListener(createConnectEvent());
    }

    private void disconnectUser() {
        stompConnectionListener.handleWebSocketDisconnectListener(createDisconnectEvent());
    }


    private SessionConnectEvent createConnectEvent() {
        Principal principal = new UsernamePasswordAuthenticationToken(USER_EMAIL, "");
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        accessor.setUser(principal);

        return new SessionConnectEvent(
                this, new GenericMessage<>(new byte[0], accessor.getMessageHeaders()), principal);
    }

    private SessionDisconnectEvent createDisconnectEvent() {
        Principal principal = new UsernamePasswordAuthenticationToken(USER_EMAIL, "");
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);

        accessor.setUser(principal);

        return new SessionDisconnectEvent(
                this,
                new GenericMessage<>(new byte[0], accessor.getMessageHeaders()),
                "sessionId",
                CloseStatus.SESSION_NOT_RELIABLE,
                principal);
    }
}
