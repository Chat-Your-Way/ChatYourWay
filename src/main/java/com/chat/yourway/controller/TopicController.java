package com.chat.yourway.controller;

import com.chat.yourway.model.Topic;
import com.chat.yourway.service.interfaces.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Tag(name = "Topic")
public class TopicController {

  private final TopicService topicService;

  @PostMapping
  public Topic create(String topicName, Principal principal){
    String email = principal.getName();
    return topicService.create(topicName, email);
  }

  @GetMapping(path = "/{id}")
  public Topic findById(@PathVariable Integer id){
    return topicService.findById(id);
  }

}
