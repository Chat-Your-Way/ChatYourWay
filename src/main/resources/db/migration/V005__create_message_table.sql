CREATE TABLE IF NOT EXISTS chat.message
(
    id          SERIAL          CONSTRAINT message_id_pkey PRIMARY KEY,
    sent_from   VARCHAR(255)    NOT NULL,
    send_to     VARCHAR(255)    DEFAULT 'ALL',
    content     TEXT            NOT NULL,
    timestamp   TIMESTAMP       NOT NULL,
    topic_id    INTEGER         NOT NULL CONSTRAINT topic_id_fkey REFERENCES chat.topic (id)
);