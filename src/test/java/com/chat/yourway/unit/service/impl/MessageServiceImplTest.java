package com.chat.yourway.unit.service.impl;

import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.repository.MessageRepository;
import com.chat.yourway.service.MessageServiceImpl;
import lombok.SneakyThrows;
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
  private static final String MAX_AMOUNT_REPORTS_FIELD_NAME = "maxAmountReports";
  private static final Byte MAX_AMOUNT_REPORTS_VALUE = 2;

  @Mock MessageRepository messageRepository;
  @InjectMocks MessageServiceImpl messageService;

  @Test
  @SneakyThrows
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

    var field = MessageServiceImpl.class.getDeclaredField(MAX_AMOUNT_REPORTS_FIELD_NAME);
    field.setAccessible(true);
    field.set(messageService, MAX_AMOUNT_REPORTS_VALUE);

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.getCountReportsByMessageId(anyInt())).thenReturn(0);

    // When
    messageService.reportMessageById(messageId, contact);

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

    var field = MessageServiceImpl.class.getDeclaredField(MAX_AMOUNT_REPORTS_FIELD_NAME);
    field.setAccessible(true);
    field.set(messageService, MAX_AMOUNT_REPORTS_VALUE);

    when(messageRepository.existsById(anyInt())).thenReturn(true);
    when(messageRepository.getCountReportsByMessageId(anyInt())).thenReturn(Integer.valueOf(MAX_AMOUNT_REPORTS_VALUE));

    // When
    messageService.reportMessageById(messageId, contact);

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
