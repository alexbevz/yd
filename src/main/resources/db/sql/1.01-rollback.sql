-- rename column
ALTER TABLE contract RENAME COLUMN id TO contract_id;

ALTER TABLE courier RENAME COLUMN id TO courier_id;

ALTER TABLE region RENAME COLUMN id TO region_id;

ALTER TABLE time_period RENAME COLUMN id TO time_period_id;

ALTER TABLE type_courier RENAME COLUMN id TO type_courier_id;
ALTER TABLE type_courier RENAME COLUMN name TO name_type;

-- change reference name

ALTER TABLE courier DROP CONSTRAINT courier_type_courier_id_fkey;
ALTER TABLE courier
    ADD FOREIGN KEY (type_courier_id) REFERENCES type_courier (type_courier_id);

ALTER TABLE contract DROP CONSTRAINT contract_region_id_fkey;
ALTER TABLE contract
    ADD FOREIGN KEY (region_id) REFERENCES region (region_id);

ALTER TABLE contract DROP CONSTRAINT contract_courier_id_fkey;
ALTER TABLE contract
    ADD FOREIGN KEY (courier_id) REFERENCES courier (courier_id);

ALTER TABLE courier_time_period DROP CONSTRAINT courier_time_period_courier_id_fkey;
ALTER TABLE courier_time_period
    ADD FOREIGN KEY (courier_id) REFERENCES courier (courier_id);

ALTER TABLE courier_time_period DROP CONSTRAINT courier_time_period_time_period_id_fkey;
ALTER TABLE courier_time_period
    ADD FOREIGN KEY (time_period_id) REFERENCES time_period (time_period_id);

ALTER TABLE contract_time_period DROP CONSTRAINT contract_time_period_contract_id_fkey;
ALTER TABLE contract_time_period
    ADD FOREIGN KEY (contract_id) REFERENCES contract (contract_id);

ALTER TABLE contract_time_period DROP CONSTRAINT contract_time_period_time_period_id_fkey;
ALTER TABLE contract_time_period
    ADD FOREIGN KEY (time_period_id) REFERENCES time_period (time_period_id);

ALTER TABLE courier_region DROP CONSTRAINT courier_region_courier_id_fkey;
ALTER TABLE courier_region
    ADD FOREIGN KEY (courier_id) REFERENCES courier (courier_id);

ALTER TABLE courier_region DROP CONSTRAINT courier_region_region_id_fkey;
ALTER TABLE courier_region
    ADD FOREIGN KEY (region_id) REFERENCES region (region_id);

-- delete constraint composite primary keys and add column with constraint primary key

ALTER TABLE courier_region DROP CONSTRAINT courier_region_pkey;
ALTER TABLE courier_region
    ADD COLUMN courier_region_id serial PRIMARY KEY;

ALTER TABLE courier_time_period DROP CONSTRAINT courier_time_period_pkey;
ALTER TABLE courier_time_period
    ADD COLUMN courier_time_period_id serial PRIMARY KEY;

ALTER TABLE contract_time_period DROP CONSTRAINT contract_time_period_pkey;
ALTER TABLE contract_time_period
    ADD COLUMN contract_time_period_id serial PRIMARY KEY;
