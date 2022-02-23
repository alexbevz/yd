ALTER TABLE type_courier
    ADD COLUMN ratio_id int REFERENCES ratio (id);

UPDATE type_courier AS tcour
SET ratio_id = (SELECT ratio.id
                FROM ratio
                WHERE tcour.profit_ratio = ratio.value);

ALTER TABLE type_courier
    ALTER COLUMN ratio_id SET NOT NULL;

ALTER TABLE type_courier
    DROP CONSTRAINT type_courier_profit_ratio_check;

ALTER TABLE type_courier
    DROP COLUMN profit_ratio;