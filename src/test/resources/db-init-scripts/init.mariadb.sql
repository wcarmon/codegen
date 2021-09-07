SET sql_mode='ANSI_QUOTES';

CREATE TABLE languages
(
    id   INTEGER NOT NULL,
    name VARCHAR(64),
    PRIMARY KEY (id)
);

INSERT INTO languages (id, name)
VALUES (1, 'russian'),
       (2, 'hindi'),
       (3, 'french');

