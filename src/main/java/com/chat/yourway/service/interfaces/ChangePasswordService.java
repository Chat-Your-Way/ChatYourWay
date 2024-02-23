package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.ChangePasswordDto;
import com.chat.yourway.dto.request.RestorePasswordDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import org.springframework.security.core.userdetails.UserDetails;

public interface ChangePasswordService {

  /**
   * Changes the password for the user's account based on the provided request and user details.
   *
   * @param request     The data transfer object (DTO) containing the necessary information for
   *                    password change.
   * @param userDetails The details of the user account for which the password change is requested.
   * @throws PasswordsAreNotEqualException If old password does not matched to current user
   *                                       password.
   */
  void changePassword(ChangePasswordDto request, UserDetails userDetails);

  /**
   * Sends a password restoration email to the specified email address. This method initiates the
   * process of sending an email to the provided email address, checking if account exists in the
   * system, generating email message for sending to user by inputted email.
   *
   * @param email      The email address of the user requesting password restoration.
   * @param clientHost The client host for generating the restoration link.
   * @throws ContactNotFoundException If the provided email does not exist in a database.
   */
  void sendEmailToRestorePassword(String email, String clientHost);

  /**
   * Restores a user's password using a provided new password and restoration token.
   *
   * <p>This method allows a user to restore their password by providing a new password along with
   * the restoration token received through the password restoration process.
   *
   * @param restorePasswordDto The data transfer object (DTO) containing the necessary information
   *                           for restore password.
   * @throws EmailTokenNotFoundException If the provided token does not exist in a database.
   */
  void restorePassword(RestorePasswordDto restorePasswordDto);

}
