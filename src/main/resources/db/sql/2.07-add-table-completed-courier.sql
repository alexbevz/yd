CREATE TABLE completed_courier
(
    id         serial PRIMARY KEY,
    courier_id int NOT NULL REFERENCES courier (id),
    rate_id    int NOT NULL REFERENCES rate (id),
    ratio_id   int NOT NULL REFERENCES ratio (id),
    UNIQUE (courier_id, rate_id, ratio_id)
);

INSERT INTO completed_courier (courier_id, rate_id, ratio_id)
SELECT DISTINCT ord.courier_id,
                (SELECT rate.id FROM rate WHERE rate.value = 500),
                tcour.ratio_id
FROM "order" AS ord
         JOIN courier AS cour
              ON ord.courier_id = cour.id
         JOIN type_courier AS tcour
              ON cour.type_courier_id = tcour.id;