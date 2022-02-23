ALTER TABLE region
    DROP CONSTRAINT region_number_region_key;
ALTER TABLE region
    DROP CONSTRAINT region_number_region_check;
ALTER TABLE region
    RENAME number_region TO number;
ALTER TABLE region
    ADD CHECK (number > 0);
ALTER TABLE region
    ADD UNIQUE (number);