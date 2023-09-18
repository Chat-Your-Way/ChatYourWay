package com.chat.yourway.integration.service.impl;

import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.repository.TopicRepository;
import com.chat.yourway.service.TopicServiceImpl;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith({PostgresExtension.class,
        RedisExtension.class})
@SpringBootTest
@TestExecutionListeners(value = {
        TransactionalTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        ResetMocksTestExecutionListener.class
})
public class TopicServiceImplTest {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TopicServiceImpl topicService;

    @Test
    @DisplayName("should return list of topics with expected size when user chose by tag id")
    @DatabaseSetup(value = "/dataset/find-topics-by-tag-id-dataset.xml", type = DatabaseOperation.INSERT)
    @DatabaseTearDown(value = "/dataset/find-topics-by-tag-id-dataset.xml", type = DatabaseOperation.DELETE)
    public void shouldReturnListOfTopicsWithExpectedSize_WhenUserChoseByTagId() {
        // Given
        var tagId = 2;

        // When
        var topics = topicService.findTopicsByTag(tagId);

        // Then
        assertAll(
                () -> assertThat(topics)
                        .withFailMessage(String.format("Expecting size of list topics equals %d", tagId))
                        .extracting(List::size)
                        .isEqualTo(2)
        );
    }

}
