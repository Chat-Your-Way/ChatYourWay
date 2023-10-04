package com.chat.yourway.integration.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;

import com.chat.yourway.service.interfaces.TopicService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import lombok.SneakyThrows;
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
public class TopicServiceImplTest {
  @Autowired TopicService topicService;

  @Test
  @SneakyThrows
  @DatabaseSetup(
      value = "/dataset/find-topics-by-topic-name-dataset.xml",
      type = DatabaseOperation.INSERT)
  @DatabaseTearDown(
      value = "/dataset/find-topics-by-topic-name-dataset.xml",
      type = DatabaseOperation.DELETE)
  @DisplayName("should return list of topics when user search topics by topic name")
  public void shouldReturnListOfTopics_whenUserSearchTopicsByTopicName() {
    var topicName = "best";
    var expectedSize = 2;

    // When
    var resultList = topicService.findTopicsByTopicName(topicName);

    // Then
    assertThat(resultList.size())
        .withFailMessage("Expecting size of list of topics equals to " + expectedSize)
        .isEqualTo(expectedSize);
  }
}
