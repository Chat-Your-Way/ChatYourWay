CREATE TABLE IF NOT EXISTS chat.contact_token
(
    token       VARCHAR(36)   NOT NULL PRIMARY KEY,
    message_type   VARCHAR(20)     NOT NULL,
    contact_id  INTEGER         NOT NULL CONSTRAINT contact_contact_token_id_fkey REFERENCES chat.contact (id) on delete cascade
);