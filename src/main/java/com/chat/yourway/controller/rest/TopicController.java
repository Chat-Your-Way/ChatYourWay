package com.chat.yourway.controller.rest;

import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.dto.response.PrivateTopicInfoResponseDto;
import com.chat.yourway.dto.response.PublicTopicInfoResponseDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.dto.response.error.ApiErrorResponseDto;
import com.chat.yourway.service.TopicService;
import com.chat.yourway.service.TopicSubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class TopicController {

    private final TopicService topicService;
    private final TopicSubscriberService topicSubscriberService;

    @Operation(
            summary = "Create new public topic",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_CREATED_TOPIC),
                    @ApiResponse(
                            responseCode = "409",
                            description = VALUE_NOT_UNIQUE,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE)
            })
    @PostMapping(path = "/create", produces = APPLICATION_JSON_VALUE)
    public TopicResponseDto create(
            @Valid @RequestBody TopicRequestDto topicRequestDto) {
        return topicService.create(topicRequestDto);
    }

    @Operation(
            summary = "Update topic",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UPDATED_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "409",
                            description = VALUE_NOT_UNIQUE,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = INVALID_VALUE)
            })
    @PutMapping(path = "/update/{id}", produces = APPLICATION_JSON_VALUE)
    public TopicResponseDto update(@PathVariable UUID id, @Valid @RequestBody TopicRequestDto topicRequestDto) {
        return topicService.update(id, topicRequestDto);
    }

    @Operation(
            summary = "Find topic by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "404",
                            description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/{name}", produces = APPLICATION_JSON_VALUE)
    public TopicResponseDto findByName(@PathVariable String name) {
        return topicService.findByName(name);
    }

    @Operation(
            summary = "Find all public topics",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/all", produces = APPLICATION_JSON_VALUE)
    public List<PublicTopicInfoResponseDto> findAllPublic() {
        return topicService.findAllPublic();
    }

    @Operation(
            summary = "Find all private topics",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/private", produces = APPLICATION_JSON_VALUE)
    public List<PrivateTopicInfoResponseDto> findAllPrivate() {
        return topicService.findAllPrivate();
    }

    @Operation(
            summary = "Delete topic by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_DELETE_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = TOPIC_NOT_ACCESS,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public void delete(@PathVariable UUID id) {
        topicService.delete(id);
    }

    @Operation(
            summary = "Subscribe to the topic",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_SUBSCRIBED),
                    @ApiResponse(
                            responseCode = "409",
                            description = ALREADY_SUBSCRIBED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PostMapping(path = "/subscribe/{topicId}", produces = APPLICATION_JSON_VALUE)
    public void subscribeToTopic(@PathVariable UUID topicId) {
        topicSubscriberService.subscribeToTopicById(topicId);
    }

    @Operation(
            summary = "Unsubscribe from the topic",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_UNSUBSCRIBED),
                    @ApiResponse(
                            responseCode = "404",
                            description = CONTACT_WASNT_SUBSCRIBED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = OWNER_CANT_UNSUBSCRIBED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PatchMapping(path = "/unsubscribe/{topicId}", produces = APPLICATION_JSON_VALUE)
    public void unsubscribeFromTopic(@PathVariable UUID topicId) {
        topicSubscriberService.unsubscribeFromTopicById(topicId);
    }

    @Operation(
            summary = "Find all subscribers to topic by topicId",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/subscribers/{topicId}", produces = APPLICATION_JSON_VALUE)
    public List<ContactResponseDto> findAllSubscribersByTopicId(@PathVariable UUID topicId) {
        return topicSubscriberService.findAllSubscribersByTopicId(topicId);
    }

    @Operation(
            summary = "Find all topics by tag name",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/all/{tag}", produces = APPLICATION_JSON_VALUE)
    public List<TopicResponseDto> findAllByTegName(@PathVariable String tag) {
        String decodedTag = URLDecoder.decode(tag, UTF_8);
        return topicService.findTopicsByTagName(decodedTag);
    }

    @Operation(
            summary = "Find all topics by topic name",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "400",
                            description = SEARCH_TOPIC_VALIDATION,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/search", produces = APPLICATION_JSON_VALUE)
    public List<TopicResponseDto> findAllByTopicName(
            @Pattern(regexp = "^[a-zA-Z0-9а-яА-ЯІіЇї]*$", message = SEARCH_TOPIC_VALIDATION) @RequestParam
            String topicName) {
        String decodeTopicName = URLDecoder.decode(topicName, UTF_8);
        return topicService.findTopicsByTopicName(decodeTopicName);
    }

    @Operation(
            summary = "Add topic to favourite",
            responses = {
                    @ApiResponse(responseCode = "204", description = SUCCESSFULLY_ADD_TOPIC_TO_FAVOURITE),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(path = "{topic-id}/favourite/add")
    public void addToFavouriteTopic(
            @PathVariable("topic-id") UUID topicId) {
        topicSubscriberService.addTopicToFavourite(topicId);
    }

    @Operation(
            summary = "Remove topic from favourite",
            responses = {
                    @ApiResponse(responseCode = "204", description = SUCCESSFULLY_REMOVE_TOPIC_FROM_FAVOURITE),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(path = "{topic-id}/favourite/remove")
    public void removeToFavouriteTopic(
            @PathVariable("topic-id") UUID topicId) {
        topicSubscriberService.removeTopicFromFavourite(topicId);
    }

    @Operation(
            summary = "Find all favourite topics of contact",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @GetMapping(path = "/favourite", produces = APPLICATION_JSON_VALUE)
    public List<PublicTopicInfoResponseDto> findAllFavouriteTopics() {
        return topicService.findAllFavouriteTopics();
    }

    @Operation(
            summary = "List of popular topics",
            responses = {
                    @ApiResponse(responseCode = "200", description = SUCCESSFULLY_FOUND_TOPIC)
            })
    @GetMapping(path = "/popular/public", produces = APPLICATION_JSON_VALUE)
    public List<PublicTopicInfoResponseDto> findAllPopularPublicTopics() {
        return topicService.findPopularPublicTopics();
    }

    @Operation(
            summary = "Complain about the topic",
            responses = {
                    @ApiResponse(responseCode = "204", description = SUCCESSFULLY_COMPLAIN_TOPIC),
                    @ApiResponse(
                            responseCode = "403",
                            description = CONTACT_UNAUTHORIZED,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = TOPIC_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class))),
                    @ApiResponse(
                            responseCode = "409",
                            description = USER_DID_NOT_SUBSCRIBED_TO_TOPIC,
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
            })
    @PatchMapping("/{topic-id}/complain")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complainTopic(@PathVariable("topic-id") UUID topicId) {
        topicSubscriberService.complainTopic(topicId);
    }
}
