package com.chat.yourway.integration.controller;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Message;
import com.chat.yourway.repository.MessageRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class,
    MockitoTestExecutionListener.class,
    ResetMocksTestExecutionListener.class
})
@DatabaseSetup(value = {
    "/dataset/mockdb/topic.xml",
    "/dataset/mockdb/message.xml",
    "/dataset/mockdb/contact.xml",
    "/dataset/mockdb/contact_message_report.xml"},
    type = CLEAN_INSERT)
public class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MessageRepository messageRepository;

  private static final String URI = "/messages/";

  //-----------------------------------
  //               GET
  //-----------------------------------

  @Test
  @DisplayName("findAllByTopicId should return empty list of all messages by topic id")
  void findAllByTopicId_shouldReturnEmptyListOfAllMessagesByTopicId() throws Exception {
    // When
    int notExistingTopicId = 99;
    var result = mockMvc.perform(get(URI + "all/" + notExistingTopicId));

    // Then
    result
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("findAllByTopicId should return list of all messages by topic id")
  public void findAllByTopicId_shouldReturnListOfAllMessagesByTopicId() throws Exception {
    // Given
    int topicId = 1;
    List<Message> messages = messageRepository.findAllByTopicId(topicId);

    // When
    var result = mockMvc.perform(get(URI + "all/" + topicId));

    // Then
    result.andExpect(status().isOk());
    assertMessagesEquals(result, messages);
  }

  //-----------------------------------
  //               POST
  //-----------------------------------

  @Test
  @DisplayName("reportMessage should report a message")
  public void reportMessage_shouldReportMessage() throws Exception {
    // Given
    int messageId = 103;
    String email = "vasil@gmail.com";

    // When
    var result = mockMvc.perform(post(URI + messageId + "/report")
        .principal(new TestingAuthenticationToken(email, null)));

    // Then
    result.andExpect(status().isOk());
  }

  @Test
  @DisplayName("reportMessage should return 404 Not Found for non-existing message")
  public void reportMessage_shouldReturn404ForNonExistingMessage() throws Exception {
    // Given
    int nonExistingMessageId = 99;
    String email = "vasil@gmail.com";

    // When
    var result = mockMvc.perform(post(URI + nonExistingMessageId + "/report")
        .principal(new TestingAuthenticationToken(email, null)));

    // Then
    result.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("reportMessage should return 400 Bad Request if message is already reported")
  public void reportMessage_shouldReturn400ForAlreadyReportedMessage() throws Exception {
    // Given
    int messageId = 100;
    String email = "vasil@gmail.com";

    // When
    var result = mockMvc.perform(post(URI + messageId + "/report")
        .principal(new TestingAuthenticationToken(email, null)));

    // Then
    result.andExpect(status().isBadRequest());
  }

  //-----------------------------------
  //         Private methods
  //-----------------------------------
  private void assertMessagesEquals(ResultActions result, List<Message> messages) throws Exception {
    result
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$[*].id").value(messages.stream()
            .map(Message::getId)
            .collect(Collectors.toList())))
        .andExpect(jsonPath("$[*].sentFrom").value(messages.stream()
            .map(Message::getSentFrom)
            .collect(Collectors.toList())))
        .andExpect(jsonPath("$[*].sendTo").value(messages.stream()
            .map(Message::getSendTo)
            .collect(Collectors.toList())))
        .andExpect(jsonPath("$[*].content").value(messages.stream()
            .map(Message::getContent)
            .collect(Collectors.toList())))
        .andExpect(jsonPath("$[*].timestamp").isNotEmpty());
  }

}
