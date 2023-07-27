CREATE TABLE IF NOT EXISTS chat.topic
(
    id          SERIAL          CONSTRAINT topic_id_pkey PRIMARY KEY,
    topic_name  VARCHAR(255)    NOT NULL UNIQUE,
    created_by  VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP       NOT NULL
);
