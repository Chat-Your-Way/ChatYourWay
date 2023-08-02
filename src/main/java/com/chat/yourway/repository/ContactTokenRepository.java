package com.chat.yourway.repository;

import com.chat.yourway.model.ContactToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactTokenRepository extends JpaRepository<ContactToken, String> {
}
