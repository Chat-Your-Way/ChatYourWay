CREATE SEQUENCE IF NOT EXISTS chat.contact_id_seq;

ALTER TABLE chat.contact
    ALTER COLUMN id SET DEFAULT nextval('chat.contact_id_seq');