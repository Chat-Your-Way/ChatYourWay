package com.chat.yourway.controller;

import static com.chat.yourway.config.openapi.OpenApiMessages.ALREADY_SUBSCRIBED;
import static com.chat.yourway.config.openapi.OpenApiMessages.CONTACT_UNAUTHORIZED;
import static com.chat.yourway.config.openapi.OpenApiMessages.CONTACT_WASNT_SUBSCRIBED;
import static com.chat.yourway.config.openapi.OpenApiMessages.INVALID_VALUE;
import static com.chat.yourway.config.openapi.OpenApiMessages.OWNER_CANT_UNSUBSCRIBED;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_CREATED_TOPIC;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_DELETE_TOPIC;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_FOUND_TOPIC;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_SUBSCRIBED;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_UNSUBSCRIBED;
import static com.chat.yourway.config.openapi.OpenApiMessages.SUCCESSFULLY_UPDATED_TOPIC;
import static com.chat.yourway.config.openapi.OpenApiMessages.TOPIC_NOT_ACCESS;
import static com.chat.yourway.config.openapi.OpenApiMessages.TOPIC_NOT_FOUND;
import static com.chat.yourway.config.openapi.OpenApiMessages.VALUE_NOT_UNIQUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.chat.yourway.dto.request.TopicPrivateRequestDto;
import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.service.interfaces.TopicService;
import com.chat.yourway.service.interfaces.TopicSubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Tag(name = "Topic")
public class TopicController {

  private final TopicService topicService;
  private final TopicSubscriberService topicSubscriberService;

  @Operation(summary = "Create new topic",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_CREATED_TOPIC),
          @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "400", description = INVALID_VALUE)
      })
  @PostMapping(path = "/create", produces = APPLICATION_JSON_VALUE)
  public TopicResponseDto create(@Valid @RequestBody TopicRequestDto topicRequestDto,
      Principal principal) {
    String email = principal.getName();
    return topicService.create(topicRequestDto, email);
  }

  @Operation(summary = "Create private topic",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_CREATED_TOPIC),
          @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "400", description = INVALID_VALUE)
      })
  @PostMapping(path = "/create/private", produces = APPLICATION_JSON_VALUE)
  public TopicResponseDto createPrivate(@Valid @RequestBody TopicPrivateRequestDto topicRequestDto,
      Principal principal) {
    String email = principal.getName();
    return topicService.createPrivate(topicRequestDto, email);
  }

  @Operation(summary = "Update topic",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UPDATED_TOPIC),
          @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "400", description = INVALID_VALUE)
      })
  @PutMapping(path = "/update/{id}", produces = APPLICATION_JSON_VALUE)
  public TopicResponseDto update(@PathVariable Integer id,
      @RequestBody TopicRequestDto topicRequestDto, Principal principal) {
    String email = principal.getName();
    return topicService.update(id, topicRequestDto, email);
  }

  @Operation(summary = "Find topic by id",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
          @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
  public TopicResponseDto findById(@PathVariable Integer id) {
    return topicService.findById(id);
  }

  @Operation(summary = "Find all public topics",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @GetMapping(path = "/all", produces = APPLICATION_JSON_VALUE)
  public List<TopicResponseDto> findAllPublic() {
    return topicService.findAllPublic();
  }

  @Operation(summary = "Delete by creator and topic id",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_DELETE_TOPIC),
          @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
  public void deleteByCreator(@PathVariable Integer id, Principal principal) {
    String email = principal.getName();
    topicService.deleteByCreator(id, email);
  }

  @Operation(summary = "Subscribe to the topic",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_SUBSCRIBED),
          @ApiResponse(responseCode = "409", description = ALREADY_SUBSCRIBED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @PostMapping(path = "/subscribe/{topicId}", produces = APPLICATION_JSON_VALUE)
  public void subscribeToTopic(@PathVariable Integer topicId, Principal principal) {
    String email = principal.getName();
    topicSubscriberService.subscribeToTopicById(email, topicId);
  }

  @Operation(summary = "Unsubscribe from the topic",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UNSUBSCRIBED),
          @ApiResponse(responseCode = "404", description = CONTACT_WASNT_SUBSCRIBED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
          @ApiResponse(responseCode = "403", description = OWNER_CANT_UNSUBSCRIBED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @PatchMapping(path = "/unsubscribe/{topicId}", produces = APPLICATION_JSON_VALUE)
  public void unsubscribeFromTopic(@PathVariable Integer topicId, Principal principal) {
    String email = principal.getName();
    topicSubscriberService.unsubscribeFromTopicById(email, topicId);
  }

  @Operation(summary = "Find all subscribers to topic by topicId",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @GetMapping(path = "/subscribers/{topicId}", produces = APPLICATION_JSON_VALUE)
  public List<ContactResponseDto> findAllSubscribersByTopicId(@PathVariable Integer topicId) {
    return topicSubscriberService.findAllSubscribersByTopicId(topicId);
  }

  @Operation(summary = "Find all topics by tag name",
      responses = {
          @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
          @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
              content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
      })
  @GetMapping(path = "/all/{tag}", produces = APPLICATION_JSON_VALUE)
  public List<TopicResponseDto> findAllByTegName(@PathVariable String tag) {
    return topicService.findTopicsByTagName(tag);
  }

  @Operation(summary = "Find all topics by topic name",
          responses = {
                  @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                  @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                          content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
          })
  @GetMapping(path = "/search", produces = APPLICATION_JSON_VALUE)
  public List<TopicResponseDto> findAllByTopicName(@RequestParam String topicName) {
    return topicService.findTopicsByTopicName(topicName);
  }
}
