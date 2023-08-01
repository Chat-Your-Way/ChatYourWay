CREATE TABLE IF NOT EXISTS chat.topic_subscriber
(
    contact_id      INTEGER     NOT NULL CONSTRAINT contact_id_fkey REFERENCES chat.contact (id) ON DELETE CASCADE,
    topic_id        INTEGER     NOT NULL CONSTRAINT topic_id_fkey REFERENCES chat.topic (id),
    subscribe_at    TIMESTAMP   NOT NULL,
    unsubscribe_at  TIMESTAMP
);
