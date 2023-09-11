package com.chat.yourway.config.openapi;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OpenApiExamples {

  public static final String NEW_CONTACT = """
      {  "nickname": "newNickname",
      "email": "newEmail",
      "avatarId" 1,
      "password": "newPassword"}""";

  public static final String LOGIN = """
      {"email": "user@gmail.com",
      "password": "user"}""";

  public static final String CHANGE_PASSWORD = """
      {"oldPassword": "12345",
      "newPassword": "qwerty"}""";

  public static final String EDIT_CONTACT_PROFILE = """
      {  "nickname": "editNickname",
      "avatarId" 1}""";

}
