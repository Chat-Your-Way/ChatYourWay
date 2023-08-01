CREATE TABLE IF NOT EXISTS chat.token
(
    id          SERIAL          CONSTRAINT token_id_pkey PRIMARY KEY,
    token       VARCHAR(2048)   NOT NULL UNIQUE ,
    token_type   VARCHAR(32)     NOT NULL DEFAULT 'BEARER',
    revoked     BOOLEAN         NOT NULL DEFAULT TRUE,
    expired     BOOLEAN         NOT NULL DEFAULT TRUE,
    contact_id  INTEGER         NOT NULL CONSTRAINT contact_id_fkey REFERENCES chat.contact (id)
);