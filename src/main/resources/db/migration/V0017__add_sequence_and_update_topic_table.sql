CREATE SEQUENCE IF NOT EXISTS chat.topic_id_seq;

ALTER TABLE chat.topic
    ALTER COLUMN id SET DEFAULT nextval('chat.topic_id_seq');