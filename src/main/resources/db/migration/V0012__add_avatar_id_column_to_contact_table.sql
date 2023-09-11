ALTER TABLE chat.contact
    ADD avatar_id smallint NOT NULL DEFAULT 1;

UPDATE chat.contact SET avatar_id = 1 where id > 0;