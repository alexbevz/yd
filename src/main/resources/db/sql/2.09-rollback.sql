ALTER TABLE "order"
    ADD COLUMN status status_order NOT NULL DEFAULT 'UNASSIGNED';
ALTER TABLE "order"
    ADD COLUMN courier_id int DEFAULT NULL REFERENCES courier (id);
ALTER TABLE "order"
    ADD COLUMN datetime_assignment timestamp DEFAULT NULL;
ALTER TABLE "order"
    ADD COLUMN datetime_realization timestamp DEFAULT NULL;
ALTER TABLE "order"
    ADD COLUMN datetime_realization_start timestamp DEFAULT NULL;
ALTER TABLE "order"
    ADD COLUMN type_courier_id int DEFAULT NULL REFERENCES type_courier (id);

UPDATE "order"
SET status              = 'ASSIGNED',
    courier_id          = acour.id,
    datetime_assignment = acour.dt_assigned
FROM assigned_order AS aord
         JOIN assigned_courier AS acour
              ON aord.assigned_courier_id = acour.id
WHERE aord.id = "order".id;

UPDATE "order"
SET status                     = 'COMPLETED',
    courier_id                 = ccour.courier_id,
    datetime_realization_start = cord.dt_start,
    datetime_realization       = cord.dt_finish,
    type_courier_id            = cour.type_courier_id
FROM completed_order AS cord
         JOIN completed_courier AS ccour
              ON cord.completed_courier_id = ccour.id
         JOIN courier AS cour
              ON ccour.courier_id = cour.id
WHERE cord.id = "order".id;