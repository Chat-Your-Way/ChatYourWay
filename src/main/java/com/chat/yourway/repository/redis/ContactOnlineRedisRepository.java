package com.chat.yourway.repository.redis;

import com.chat.yourway.model.redis.ContactOnline;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactOnlineRedisRepository extends CrudRepository<ContactOnline, String> {

    List<ContactOnline> findAllByTopicId(UUID topicId);

}
