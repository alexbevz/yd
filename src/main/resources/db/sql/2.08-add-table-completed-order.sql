CREATE TABLE completed_order
(
    id                   int PRIMARY KEY REFERENCES "order" (id),
    completed_courier_id int REFERENCES completed_courier (id),
    dt_start             timestamp NOT NULL,
    dt_finish            timestamp NOT NULL
);

INSERT INTO completed_order
SELECT ord.id, ccour.id, ord.datetime_realization_start, ord.datetime_realization
FROM "order" AS ord
         JOIN courier AS cour
              ON ord.courier_id = cour.id
         JOIN completed_courier AS ccour
              ON cour.id = ccour.courier_id
WHERE ord.status = 'COMPLETED';