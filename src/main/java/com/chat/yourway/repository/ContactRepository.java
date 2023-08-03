package com.chat.yourway.repository;

import com.chat.yourway.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link ContactRepository}
 *
 * @author Dmytro Trotsenko on 7/26/23
 */
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    Optional<Contact> findByEmail(String email);

}
