create table if not exists chat.contact_message_report(
                                             contact_id            INTEGER        NOT NULL,
                                             message_id            INTEGER        NOT NULL,
                                             CONSTRAINT report_id_pkey PRIMARY KEY (contact_id, message_id),
    CONSTRAINT report_message_id_fkey FOREIGN KEY (message_id) REFERENCES chat.message(id) ON DELETE CASCADE,
    CONSTRAINT report_contact_id_fkey FOREIGN KEY (contact_id) REFERENCES chat.contact(id) ON DELETE CASCADE
    );