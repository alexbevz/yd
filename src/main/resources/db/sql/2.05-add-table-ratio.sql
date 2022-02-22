CREATE TABLE ratio
(
    id    serial PRIMARY KEY,
    value real NOT NULL UNIQUE CHECK ( value > 0 )
);

INSERT INTO ratio (value)
SELECT tcour.profit_ratio
FROM type_courier AS tcour;