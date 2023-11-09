ALTER TABLE chat.topic_subscriber
    ADD is_favourite_topic boolean DEFAULT false;

UPDATE chat.topic_subscriber
SET is_favourite_topic = false
where contact_id > 0;