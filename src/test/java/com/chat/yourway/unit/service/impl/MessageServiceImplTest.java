package com.chat.yourway.unit.service.impl;

import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.MessageRepository;
import com.chat.yourway.service.MessageServiceImpl;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {
  @Mock MessageRepository messageRepository;
  @InjectMocks MessageServiceImpl messageService;

  @Test
  @DisplayName("reportMessageById should save report to message when user makes report")
  public void reportMessageById_shouldSaveReportToMessage_WhenUserMakesReport() {
    // Given
    var messageId = 1;
    var contact =
        Contact.builder()
            .id(1)
            .nickname("username12353")
            .avatarId((byte) 1)
            .email("user@gmail.com")
            .password("oldPassword")
            .isActive(true)
            .isPrivate(true)
            .build();

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.getCountReportsByMessageId(anyInt())).thenReturn(0);

    // When
    messageService.reportMessageById(messageId, contact);

    // Then
    verify(messageRepository, never()).deleteById(anyInt());
    verify(messageRepository).saveReportFromContactToMessage(anyString(), anyInt());
  }

  @Test
  @DisplayName(
      "reportMessageById should delete message when user makes report and message reached max attempts")
  @DatabaseSetup(value = "/dataset/report-to-message-dataset.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/report-to-message-dataset.xml",
      type = DatabaseOperation.DELETE)
  public void
      reportMessageById_shouldDeleteMessage_WhenUserMakesReportAndMessageReachedMaxAttempts() {
    // Given
    var messageId = 1;
    var contact =
        Contact.builder()
            .id(1)
            .nickname("username12353")
            .avatarId((byte) 1)
            .email("user@gmail.com")
            .password("oldPassword")
            .isActive(true)
            .isPrivate(true)
            .build();

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.getCountReportsByMessageId(anyInt())).thenReturn(2);

    // When
    messageService.reportMessageById(messageId, contact);

    // Then
    verify(messageRepository).deleteById(anyInt());
    verify(messageRepository, never()).saveReportFromContactToMessage(anyString(), anyInt());
  }

  @Test
  @DisplayName(
      "reportMessageById should throw MessageHasAlreadyReportedException when user makes report again")
  @DatabaseSetup(value = "/dataset/report-to-message-dataset.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/report-to-message-dataset.xml",
      type = DatabaseOperation.DELETE)
  public void reportMessageById_shouldThrowMessageHasAlreadyReportedException_WhenUserMakesReportAgain() {
    // Given
    var messageId = 1;
    var contact =
        Contact.builder()
            .id(1)
            .nickname("username12353")
            .avatarId((byte) 1)
            .email("user@gmail.com")
            .password("oldPassword")
            .isActive(true)
            .isPrivate(true)
            .build();

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.hasReportByContactEmailAndMessageId(anyString(), anyInt()))
        .thenReturn(true);

    // When
    // Then
    assertThrows(
        MessageHasAlreadyReportedException.class,
        () -> messageService.reportMessageById(messageId, contact));

    verify(messageRepository, never()).deleteById(anyInt());
    verify(messageRepository, never()).saveReportFromContactToMessage(anyString(), anyInt());
  }
}
