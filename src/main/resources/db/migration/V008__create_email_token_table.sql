CREATE TABLE IF NOT EXISTS chat.email_token
(
    token       VARCHAR(36)   NOT NULL PRIMARY KEY,
    message_type   VARCHAR(20)     NOT NULL,
    contact_id  INTEGER         NOT NULL CONSTRAINT fk_email_token_contact REFERENCES chat.contact (id) on delete cascade
);