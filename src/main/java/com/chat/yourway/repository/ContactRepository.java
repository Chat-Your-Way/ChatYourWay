package com.chat.yourway.repository;

import com.chat.yourway.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

  Optional<Contact> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

}
