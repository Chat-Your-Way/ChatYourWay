package com.chat.yourway.config.openapi;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OpenApiExamples {

  public static final String NEW_CONTACT = """
      {  "username": "newNickname",
      "email": "newEmail",
      "password": "newPassword"}""";

  public static final String LOGIN = """
      {"email": "user@gmail.com",
      "password": "user"}""";

  public static final String CHANGE_PASSWORD = """
      {"oldPassword": "12345",
      "newPassword": "qwerty"}""";

}
