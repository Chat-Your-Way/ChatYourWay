
create TABLE IF NOT EXISTS chat.topic_complaints
(
    contact_id      uuid       NOT NULL,
    topic_id        uuid       NOT NULL,
	CONSTRAINT fk_topiccomplaints_contacts FOREIGN KEY(contact_id) REFERENCES chat.contacts(id),
	CONSTRAINT fk_topiccomplaints_topics FOREIGN KEY(topic_id) REFERENCES chat.topics(id)
);
