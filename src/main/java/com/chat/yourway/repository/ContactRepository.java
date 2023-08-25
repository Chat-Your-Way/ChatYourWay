package com.chat.yourway.repository;

import com.chat.yourway.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

  Optional<Contact> findByEmail(String email);

  @Modifying
  @Query("UPDATE Contact c set c.password = :password where c.email = :email")
  void changePasswordByEmail(String password, String email);


  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

}
