CREATE TABLE rate
(
    id    serial PRIMARY KEY,
    value real NOT NULL UNIQUE CHECK ( value > 0 )
);

INSERT INTO rate
VALUES (1, 500);