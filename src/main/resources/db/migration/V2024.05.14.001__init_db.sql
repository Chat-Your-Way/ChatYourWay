create SCHEMA IF NOT EXISTS chat;

create TABLE IF NOT EXISTS chat.contacts
(
    id            uuid          PRIMARY KEY,
	nickname      VARCHAR(255)  NOT NULL UNIQUE,
    password      VARCHAR(2048) NOT NULL,
	email 		  VARCHAR(255) 	NOT NULL UNIQUE,
	role 		  VARCHAR(50) 	NOT NULL DEFAULT 'USER',
	avatar_id     smallint NOT NULL DEFAULT 1,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    is_permitted_sending_private_message BOOLEAN NOT NULL DEFAULT TRUE
);
create index idx_contacts_email on chat.contacts (email);
create index idx_contacts_nickname on chat.contacts (nickname);

create TABLE IF NOT EXISTS chat.email_tokens
(
    token       	uuid		NOT NULL PRIMARY KEY,
    message_type    VARCHAR(50)	NOT NULL,
    contact_id  	uuid        NOT NULL,
	CONSTRAINT fk_emailtokens_contacts FOREIGN KEY(contact_id) REFERENCES chat.contacts(id)
);

create TABLE IF NOT EXISTS chat.topics
(
    id          uuid            PRIMARY KEY,
    topic_name  VARCHAR(255)    NOT NULL UNIQUE,
    created_by  uuid            NOT NULL,
    created_at  TIMESTAMP       NOT NULL,
	scope		VARCHAR(50)     NOT NULL DEFAULT 'PUBLIC',
	CONSTRAINT fk_topic_contacts FOREIGN KEY(created_by) REFERENCES chat.contacts(id)
);

create TABLE IF NOT EXISTS chat.topic_contacts
(
    contact_id      uuid       NOT NULL,
    topic_id        uuid       NOT NULL,
	CONSTRAINT fk_topiccontacts_contacts FOREIGN KEY(contact_id) REFERENCES chat.contacts(id),
	CONSTRAINT fk_topiccontacts_topics FOREIGN KEY(topic_id) REFERENCES chat.topics(id)
);

create TABLE IF NOT EXISTS chat.topic_messages
(
    id               uuid         PRIMARY KEY,
	timestamp        TIMESTAMP    NOT NULL,
	topic_id         uuid         NOT NULL,
    send_by		     uuid	      NOT NULL,
    message_text     TEXT         NOT NULL,
	CONSTRAINT fk_topicmessages_contacts FOREIGN KEY(send_by) REFERENCES chat.contacts(id),
	CONSTRAINT fk_topicmessages_topics FOREIGN KEY(topic_id) REFERENCES chat.topics(id)
);
create index idx_topicmessages_topic_id on chat.topic_messages (topic_id);
create index idx_topicmessages_send_by on chat.topic_messages (send_by);

create TABLE IF NOT EXISTS chat.contact_report_messages
(
    contact_id		uuid        NOT NULL,
    message_id		uuid		NOT NULL,
	CONSTRAINT fk_contactreportmessages_contacts FOREIGN KEY (contact_id) REFERENCES chat.contacts(id),
    CONSTRAINT fk_contactreportmessages_messages FOREIGN KEY (message_id) REFERENCES chat.topic_messages(id)
);

create TABLE IF NOT EXISTS chat.contact_favorite_topics
(
    contact_id		uuid        NOT NULL,
    topic_id		uuid		NOT NULL,
	CONSTRAINT pk_contactfavoritetopics PRIMARY KEY (contact_id, topic_id),
	CONSTRAINT pk_contactfavoritetopics_contacts FOREIGN KEY (contact_id) REFERENCES chat.contacts(id),
    CONSTRAINT pk_contactfavoritetopics_topics FOREIGN KEY (topic_id) REFERENCES chat.topics(id)
);

create TABLE IF NOT EXISTS chat.tags
(
    id          uuid          PRIMARY KEY,
	name      	VARCHAR(255)  NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS chat.topic_tags
(
    topic_id    uuid          NOT NULL,
	tag_id      uuid          NOT NULL,
	CONSTRAINT pk_topictags PRIMARY KEY (topic_id, tag_id),
	CONSTRAINT fk_topictags_topics FOREIGN KEY (topic_id) REFERENCES chat.topics(id),
	CONSTRAINT fk_topictags_tags FOREIGN KEY (tag_id) REFERENCES chat.tags(id)
);
