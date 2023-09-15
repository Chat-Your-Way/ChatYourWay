CREATE TABLE IF NOT EXISTS chat.tag
(
    id            SERIAL        CONSTRAINT tag_id_pkey PRIMARY KEY,
    name      VARCHAR(100)  NOT NULL UNIQUE
);

create table if not exists chat.topic_tag(
    topic_id            INTEGER        NOT NULL,
    tag_id            INTEGER        NOT NULL,
    CONSTRAINT topic_id_tag_id_pkey PRIMARY KEY (topic_id, tag_id),
    CONSTRAINT topic_tag_topic_topic_id_fkey FOREIGN KEY (topic_id) REFERENCES chat.topic (id),
    CONSTRAINT topic_tag_tag_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES chat.tag (id)
    );