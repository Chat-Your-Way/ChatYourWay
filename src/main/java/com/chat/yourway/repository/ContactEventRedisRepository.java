package com.chat.yourway.repository;

import com.chat.yourway.model.event.ContactEvent;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactEventRedisRepository extends CrudRepository<ContactEvent, String> {

  List<ContactEvent> findAllByEmail(String email);

  List<ContactEvent> findAllByTopicId(Integer topicId);

}
