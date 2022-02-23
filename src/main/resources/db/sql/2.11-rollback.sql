ALTER TABLE region
    DROP CONSTRAINT region_number_key;
ALTER TABLE region
    DROP CONSTRAINT region_number_check;
ALTER TABLE region
    RENAME number TO number_region;
ALTER TABLE region
    ADD CHECK (number_region > 0);
ALTER TABLE region
    ADD UNIQUE (number_region);