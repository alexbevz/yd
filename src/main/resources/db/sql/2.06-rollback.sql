ALTER TABLE type_courier
    ADD COLUMN profit_ratio real CHECK ( profit_ratio > 0 );

UPDATE type_courier AS tcour
SET profit_ratio = (SELECT ratio.value
                    FROM ratio
                    WHERE tcour.ratio_id = ratio.id);

ALTER TABLE type_courier
    ALTER COLUMN profit_ratio SET NOT NULL;

ALTER TABLE type_courier
    DROP CONSTRAINT type_courier_ratio_id_fkey;

ALTER TABLE type_courier
    DROP COLUMN ratio_id;