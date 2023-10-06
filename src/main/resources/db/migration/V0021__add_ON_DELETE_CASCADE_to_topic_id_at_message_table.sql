ALTER TABLE chat.message
    DROP CONSTRAINT topic_id_fkey;

ALTER TABLE chat.message
    ADD CONSTRAINT topic_id_fkey FOREIGN KEY (topic_id) REFERENCES chat.topic (id) ON DELETE CASCADE;