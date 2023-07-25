CREATE TABLE IF NOT EXISTS chat.contact
(
    id            SERIAL        CONSTRAINT contact_id_pkey PRIMARY KEY,
    username      VARCHAR(255)  NOT NULL UNIQUE,
    password      VARCHAR(2048) NOT NULL,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    is_private    BOOLEAN       NOT NULL DEFAULT TRUE
);
