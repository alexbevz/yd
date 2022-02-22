-- для триггера
SELECT coalesce(MAX(cord.dt_finish), acour.dt_assigned)
FROM completed_order AS cord
         JOIN completed_courier AS ccour
              ON cord.completed_courier_id = ccour.id
         JOIN assigned_courier AS acour
              ON ccour.courier_id = acour.id
WHERE cord.dt_finish > acour.dt_assigned);
-- для триггера

-- косметические изменения
ALTER TABLE region DROP CONSTRAINT region_number_region_key;
ALTER TABLE region DROP CONSTRAINT region_number_region_check;
ALTER TABLE region RENAME number_region TO number;
ALTER TABLE region ADD CHECK (number > 0);
ALTER TABLE region ADD UNIQUE (number);
-- косметические изменения