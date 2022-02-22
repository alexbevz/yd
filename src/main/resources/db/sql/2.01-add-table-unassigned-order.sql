CREATE TABLE unassigned_order
(
    id serial PRIMARY KEY REFERENCES "order" (id)
);

INSERT INTO unassigned_order (id)
SELECT ord.id
FROM "order" AS ord
WHERE ord.status = 'UNASSIGNED';