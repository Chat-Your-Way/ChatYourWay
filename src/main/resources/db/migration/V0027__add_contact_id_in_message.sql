ALTER TABLE chat.message
  ADD sender_id int4,
  ALTER COLUMN sender_id SET DEFAULT NULL,
  ADD CONSTRAINT sender_fk_contact FOREIGN KEY (sender_id) REFERENCES chat.contact (id);
ALTER TABLE chat.message
  ADD receiver_id int4,
  ALTER COLUMN receiver_id SET DEFAULT NULL,
  ADD CONSTRAINT receiver_fk_contact FOREIGN KEY (receiver_id) REFERENCES chat.contact (id);

UPDATE chat.message m
SET sender_id = (SELECT ID FROM chat.contact c WHERE c.email = m.sent_from),
  receiver_id = (SELECT ID FROM chat.contact c WHERE c.email = m.send_to);