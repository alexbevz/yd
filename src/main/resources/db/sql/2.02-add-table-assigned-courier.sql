CREATE TABLE assigned_courier
(
    id          int PRIMARY KEY REFERENCES courier (id),
    dt_assigned timestamp NOT NULL DEFAULT now()
);

INSERT INTO assigned_courier (id, dt_assigned)
SELECT DISTINCT ord.courier_id, ord.datetime_assignment
FROM "order" AS ord
WHERE ord.status = 'ASSIGNED'
GROUP BY ord.courier_id, ord.datetime_assignment;