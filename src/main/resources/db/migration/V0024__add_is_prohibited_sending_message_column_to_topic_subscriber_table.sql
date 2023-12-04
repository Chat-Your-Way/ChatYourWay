ALTER TABLE chat.topic_subscriber
    ADD is_permitted_sending_message boolean DEFAULT true;

UPDATE chat.topic_subscriber
SET is_permitted_sending_message = true
where contact_id > 0;