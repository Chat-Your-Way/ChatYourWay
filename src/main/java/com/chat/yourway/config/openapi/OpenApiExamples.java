package com.chat.yourway.config.openapi;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OpenApiExamples {

  public static final String NEW_CONTACT = """
      {
      "nickname": "newNickname",
      "email": "newemail@gmail.com",
      "avatarId": 1,
      "password": "Password-123"
      }""";

  public static final String LOGIN = """
      {
      "email": "newemail@gmail.com",
      "password": "Password-123"
      }""";

  public static final String CHANGE_PASSWORD = """
      {
      "oldPassword": "Password-123",
      "newPassword": "Password-321"
      }""";

  public static final String EDIT_CONTACT_PROFILE = """
      {
      "nickname": "editNickname",
      "avatarId": 2
      }""";

  public static final String NEW_TOPIC = """
      {
      "topicName": "new chat topic",
      "tags": ["#tag1", "#tag2"]
      }""";
}
