package com.chat.yourway.repository;

import com.chat.yourway.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link TokenRedisRepository}
 *
 * @author Dmytro Trotsenko on 7/28/23
 */

@Repository
public interface TokenRedisRepository extends CrudRepository<Token, String> {

    List<Token> findAllByEmail(String email);

    Optional<Token> findByToken(String token);

}
