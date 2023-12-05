ALTER TABLE chat.contact
    ADD is_permitted_sending_private_message boolean DEFAULT true;

UPDATE chat.contact
SET is_permitted_sending_private_message = true
where id > 0;

ALTER TABLE chat.topic_subscriber
DROP COLUMN is_permitted_sending_message;