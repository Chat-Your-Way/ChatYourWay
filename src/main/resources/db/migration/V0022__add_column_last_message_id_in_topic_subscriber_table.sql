ALTER TABLE chat.topic_subscriber
    ADD last_message_id INTEGER;

ALTER TABLE chat.topic_subscriber
    ADD CONSTRAINT topic_subscriber_last_message_id_fkey
        FOREIGN KEY (last_message_id) REFERENCES chat.topic (id);
