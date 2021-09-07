CREATE TABLE food
(
    id   INTEGER NOT NULL,
    name VARCHAR(64),
    PRIMARY KEY (id)
);

INSERT INTO food (id, name)
VALUES (1, 'tangerine'),
       (2, 'cheese');
;
