CREATE TABLE assigned_order
(
    id                  serial PRIMARY KEY REFERENCES "order" (id),
    assigned_courier_id int NOT NULL REFERENCES assigned_courier (id)
);

INSERT INTO assigned_order (id, assigned_courier_id)
SELECT ord.id, ord.courier_id
FROM "order" AS ord
WHERE ord.status = 'ASSIGNED';