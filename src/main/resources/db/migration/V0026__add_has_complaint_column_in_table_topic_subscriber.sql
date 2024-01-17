ALTER TABLE chat.topic_subscriber
    ADD has_complaint boolean DEFAULT false;

UPDATE chat.topic_subscriber
SET has_complaint = false
where contact_id > 0;