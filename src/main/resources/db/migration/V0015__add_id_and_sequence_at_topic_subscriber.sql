CREATE SEQUENCE IF NOT EXISTS chat.topic_subscriber_id_seq;

ALTER TABLE chat.topic_subscriber
    ADD COLUMN IF NOT EXISTS id INTEGER PRIMARY KEY DEFAULT nextval('chat.topic_subscriber_id_seq');
