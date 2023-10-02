ALTER TABLE chat.topic
    ADD is_public BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_is_public ON chat.topic (is_public);
CREATE INDEX idx_topic_name ON chat.topic (topic_name);

UPDATE chat.topic SET is_public = TRUE where id > 0;