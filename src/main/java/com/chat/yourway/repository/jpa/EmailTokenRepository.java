package com.chat.yourway.repository.jpa;

import com.chat.yourway.model.email.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, UUID> {
}
