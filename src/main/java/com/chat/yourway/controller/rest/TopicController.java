package com.chat.yourway.controller.rest;

import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.dto.response.PrivateTopicInfoResponseDto;
import com.chat.yourway.dto.response.PublicTopicInfoResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.dto.response.error.ApiErrorResponseDto;
import com.chat.yourway.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

import static com.chat.yourway.config.openapi.OpenApiMessages.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Tag(name = "Topic")
public class TopicController {

    private final TopicService topicService;

    private static final String CREATE = "/create";
    private static final String UPDATE_ID = "/update/{id}";
    private static final String TOPIC_GET_ID = "/{id}";
    private static final String TOPIC_ALL = "/all";
    private static final String TOPIC_PRIVATE = "/private";
    private static final String DELETE_TOPIC_ID = "/{id}";
    private static final String SUBSCRIBE_TOPIC_ID = "/subscribe/{topicId}";
    private static final String UNSUBSCRIBE_TOPIC_ID = "/unsubscribe/{topicId}";
    private static final String SUBSCRIBERS_TOPIC_ID = "/subscribers/{topicId}";
    private static final String ALL_TAG = "/all/{tag}";
    private static final String SEARCH = "/search";
    private static final String TOPIC_ID_FAVOURITE_ADD = "{topic-id}/favourite/add";
    private static final String TOPIC_ID_FAVOURITE_REMOVE = "{topic-id}/favourite/remove";
    private static final String FAVOURITE = "/favourite";
    private static final String FILE_PATH = "/popular/public";
    private static final String TOPIC_ID_COMPLAIN = "/{topic-id}/complain";

    @Operation(summary = "Create new public topic", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_CREATED_TOPIC),
                    @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE)
            })
    @PostMapping(path = CREATE, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public TopicResponseDto create(@Valid @RequestBody TopicRequestDto topicRequestDto) {
        return topicService.create(topicRequestDto);
    }

    @Operation(summary = "Update topic", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UPDATED_TOPIC),
                    @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = VALUE_NOT_UNIQUE,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE)
            })
    @PutMapping(path = UPDATE_ID, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public TopicResponseDto update(@PathVariable UUID id, @Valid @RequestBody TopicRequestDto topicRequestDto) {
        return topicService.update(id, topicRequestDto);
    }

    @Operation(summary = "Find topic by id", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = TOPIC_GET_ID, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public TopicResponseDto findById(@PathVariable UUID id) {
        return topicService.findById(id);
    }

    @Operation(summary = "Find all public topics", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = TOPIC_ALL, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public List<PublicTopicInfoResponseDto> findAllPublic() {
        return topicService.findAllPublic();
    }

    @Operation(summary = "Find all private topics", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = TOPIC_PRIVATE, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public List<PrivateTopicInfoResponseDto> findAllPrivate() {
        return topicService.findAllPrivate();
    }

    @Operation(summary = "Delete topic by id", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_DELETE_TOPIC),
                    @ApiResponse(responseCode = "403", description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @DeleteMapping(path = DELETE_TOPIC_ID, consumes = APPLICATION_JSON_VALUE)
    public void delete(@PathVariable UUID id) {
        topicService.delete(id);
    }

    @Operation(summary = "Subscribe to the topic", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_SUBSCRIBED),
                    @ApiResponse(responseCode = "409", description = ALREADY_SUBSCRIBED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(path = SUBSCRIBE_TOPIC_ID, consumes = APPLICATION_JSON_VALUE)
    public void subscribeToTopic(@PathVariable UUID topicId) {
        topicService.subscribeToTopic(topicId);
    }

    @Operation(summary = "Unsubscribe from the topic", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UNSUBSCRIBED),
                    @ApiResponse(responseCode = "404", description = CONTACT_WASNT_SUBSCRIBED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = OWNER_CANT_UNSUBSCRIBED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PatchMapping(path = UNSUBSCRIBE_TOPIC_ID, consumes = APPLICATION_JSON_VALUE)
    public void unsubscribeFromTopic(@PathVariable UUID topicId) {
        topicService.unsubscribeFromTopic(topicId);
    }

    @Operation(summary = "Find all subscribers to topic by topicId", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = SUBSCRIBERS_TOPIC_ID, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public List<ContactResponseDto> findAllSubscribersByTopicId(@PathVariable UUID topicId) {
        return topicService.findAllSubscribersByTopicId(topicId);
    }

    @Operation(summary = "Find all topics by tag name", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = ALL_TAG, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public List<TopicResponseDto> findAllByTegName(@PathVariable String tag) {
        return topicService.findTopicsByTagName(URLDecoder.decode(tag, UTF_8));
    }

    @Operation(summary = "Find all topics by topic name", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "400", description = SEARCH_TOPIC_VALIDATION,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = SEARCH, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public List<TopicResponseDto> findAllByTopicName(
            @Pattern(regexp = "^[a-zA-Z0-9а-яА-ЯІіЇї]*$", message = SEARCH_TOPIC_VALIDATION)
            @RequestParam String topicName) {
        return topicService.findTopicsByTopicName(URLDecoder.decode(topicName, UTF_8));
    }

    @Operation(summary = "Add topic to favourite", responses = {
                    @ApiResponse(responseCode = "204", description = SUCCESSFULLY_ADD_TOPIC_TO_FAVOURITE),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(path = TOPIC_ID_FAVOURITE_ADD, consumes = APPLICATION_JSON_VALUE)
    public void addToFavouriteTopic(@PathVariable("topic-id") UUID topicId) {
        topicService.addTopicToFavourite(topicId);
    }

    @Operation(summary = "Remove topic from favourite", responses = {
                    @ApiResponse(responseCode = "204", description = SUCCESSFULLY_REMOVE_TOPIC_FROM_FAVOURITE),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(path = TOPIC_ID_FAVOURITE_REMOVE, consumes = APPLICATION_JSON_VALUE)
    public void removeToFavouriteTopic(@PathVariable("topic-id") UUID topicId) {
        topicService.removeTopicFromFavourite(topicId);
    }

    @Operation(summary = "Find all favourite topics of contact", responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = FAVOURITE, produces = APPLICATION_JSON_VALUE)
    public List<PublicTopicInfoResponseDto> findAllFavouriteTopics() {
        return topicService.findAllFavouriteTopics();
    }

    @Operation(summary = "List of popular topics", responses = {
                @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC)
            })
    @GetMapping(path = FILE_PATH, produces = APPLICATION_JSON_VALUE)
    public List<PublicTopicInfoResponseDto> findAllPopularPublicTopics() {
        return topicService.findPopularPublicTopics();
    }

    @Operation(summary = "Complain about the topic", responses = {
                    @ApiResponse(responseCode = "204", description = SUCCESSFULLY_COMPLAIN_TOPIC),
                    @ApiResponse(responseCode = "403", description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = USER_DID_NOT_SUBSCRIBED_TO_TOPIC,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = TOPIC_ID_COMPLAIN, consumes = APPLICATION_JSON_VALUE)
    public void complainTopic(@PathVariable("topic-id") UUID topicId) {
        topicService.complainTopic(topicId);
    }
}
