package com.chat.yourway.integration.service.impl;

import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.repository.ContactRepository;
import com.chat.yourway.repository.MessageRepository;
import com.chat.yourway.service.MessageServiceImpl;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class MessageServiceImplTest {
  @Autowired ContactRepository contactRepository;
  @Autowired MessageRepository messageRepository;
  @Autowired MessageServiceImpl messageService;

  @Test
  @DisplayName("should save report to message when user makes report")
  @DatabaseSetup(value = "/dataset/report-to-message-dataset.xml", type = DatabaseOperation.CLEAN_INSERT)
  @DatabaseTearDown(
      value = "/dataset/report-to-message-dataset.xml",
      type = DatabaseOperation.DELETE)
  public void shouldSaveReportToMessage_WhenUserMakesReport() {
    // Given
    var messageId = 2;
    var email = "user3@gmail.com";

    // When
    messageService.reportMessageById(messageId, email);

    var countReports = messageRepository.getCountReportsByMessageId(messageId);
    // Then
    assertAll(
        () ->
            assertThat(countReports)
                .withFailMessage("Expecting size of list of contact reports greater than 0")
                .isEqualTo(1));
  }

  @Test
  @DisplayName("should delete message when user makes report and message reached max attempts")
  @DatabaseSetup(value = "/dataset/report-to-message-dataset.xml", type = DatabaseOperation.CLEAN_INSERT)
  @DatabaseTearDown(
      value = "/dataset/report-to-message-dataset.xml",
      type = DatabaseOperation.DELETE)
  public void shouldDeleteMessage_WhenUserMakesReportAndMessageReachedMaxAttempts() {
    // Given
    var messageId = 1;
    var email = "user3@gmail.com";

    // When
    messageService.reportMessageById(messageId, email);

    var message = messageRepository.findById(messageId);

    // Then
    assertAll(
        () -> assertThat(message).withFailMessage("Expecting message does not exist").isEmpty());
  }

  @Test
  @DisplayName("should throw MessageHasAlreadyReportedException when user makes report again")
  @DatabaseSetup(value = "/dataset/report-to-message-dataset.xml", type = DatabaseOperation.CLEAN_INSERT)
  @DatabaseTearDown(
      value = "/dataset/report-to-message-dataset.xml",
      type = DatabaseOperation.DELETE)
  public void shouldThrowMessageHasAlreadyReportedException_WhenUserMakesReportAgain() {
    // Given
    var messageId = 1;
    var email = "user2@gmail.com";

    // When
    // Then
    assertThrows(
        MessageHasAlreadyReportedException.class,
        () -> messageService.reportMessageById(messageId, email));
  }
}
