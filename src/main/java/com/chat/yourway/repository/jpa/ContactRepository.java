package com.chat.yourway.repository.jpa;

import com.chat.yourway.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

  Optional<Contact> findByEmailIgnoreCase(String email);

  @Modifying
  @Query("UPDATE Contact c set c.password = :password where c.email = :email")
  void changePasswordByEmail(String password, String email);


  boolean existsByEmailIgnoreCase(String email);

  @Modifying
  @Query(
          nativeQuery = true,
          value =
                  "UPDATE chat.contacts "
                          + "SET is_permitted_sending_private_message = :isPermittedSendingPrivateMessage "
                          + "WHERE email = :contactEmail")
  void updatePermissionSendingPrivateMessageByContactEmail(
          @Param("contactEmail") String contactEmail,
          @Param("isPermittedSendingPrivateMessage") boolean isPermittedSendingPrivateMessage);
}
