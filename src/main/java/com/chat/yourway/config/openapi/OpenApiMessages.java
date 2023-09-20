package com.chat.yourway.config.openapi;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OpenApiMessages {

  public static final String SUCCESSFULLY_REGISTERED = "Contact successfully registered";
  public static final String VALUE_NOT_UNIQUE = "The field value is not unique";
  public static final String ERR_SENDING_EMAIL = "Sending email error";
  public static final String SUCCESSFULLY_AUTHORIZATION = "Contact successfully logged in";
  public static final String CONTACT_NOT_FOUND = "Contact wasn't found in repository";
  public static final String CONTACT_UNAUTHORIZED = "Contact authorization error";
  public static final String SUCCESSFULLY_REFRESHED_TOKEN = "Contact successfully refreshed token";
  public static final String SUCCESSFULLY_ACTIVATED_ACCOUNT = "Contact account successfully activated";
  public static final String EMAIL_TOKEN_NOT_FOUND = "Email token for contact wasn't found in repository";
  public static final String SUCCESSFULLY_CHANGING_PASSWORD = "Contact successfully changing password";
  public static final String INVALID_OLD_PASSWORD= "Old password is not correct";
  public static final String SUCCESSFULLY_SEND_REQUEST_RESTORE_PASSWORD = "Contact successfully sent request to restored password";
  public static final String SUCCESSFULLY_RESTORED_PASSWORD = "Contact successfully restored password";
  public static final String SUCCESSFULLY_UPDATED_CONTACT_PROFILE = "Contact profile successfully updated";
  public static final String SUCCESSFULLY_REPORTED_MESSAGE = "Message is reported successfully";
  public static final String MESSAGE_NOT_FOUND = "Message wasn't found in repository";
  public static final String MESSAGE_HAS_ALREADY_REPORTED = "You have already made report";

}
