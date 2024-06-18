
create TABLE IF NOT EXISTS chat.unread_messages
(
    contact_id      uuid       NOT NULL,
    message_id        uuid       NOT NULL,
	CONSTRAINT fk_unreadmessages_contacts FOREIGN KEY(contact_id) REFERENCES chat.contacts(id),
	CONSTRAINT fk_unreadmessages_messages FOREIGN KEY(message_id) REFERENCES chat.topic_messages(id)
);
