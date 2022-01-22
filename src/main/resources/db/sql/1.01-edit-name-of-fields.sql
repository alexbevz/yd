-- rename column
ALTER TABLE contract RENAME COLUMN contract_id TO id;

ALTER TABLE courier RENAME COLUMN courier_id TO id;

ALTER TABLE region RENAME COLUMN region_id TO id;

ALTER TABLE time_period RENAME COLUMN time_period_id TO id;

ALTER TABLE type_courier RENAME COLUMN type_courier_id TO id;
ALTER TABLE type_courier RENAME COLUMN name_type TO name;

-- change reference name

ALTER TABLE courier DROP CONSTRAINT courier_type_courier_id_fkey;
ALTER TABLE courier
    ADD FOREIGN KEY (type_courier_id) REFERENCES type_courier (id);

ALTER TABLE contract DROP CONSTRAINT contract_region_id_fkey;
ALTER TABLE contract
    ADD FOREIGN KEY (region_id) REFERENCES region (id);

ALTER TABLE contract DROP CONSTRAINT contract_courier_id_fkey;
ALTER TABLE contract
    ADD FOREIGN KEY (courier_id) REFERENCES courier (id);

ALTER TABLE courier_time_period DROP CONSTRAINT courier_time_period_courier_id_fkey;
ALTER TABLE courier_time_period
    ADD FOREIGN KEY (courier_id) REFERENCES courier (id);

ALTER TABLE courier_time_period DROP CONSTRAINT courier_time_period_time_period_id_fkey;
ALTER TABLE courier_time_period
    ADD FOREIGN KEY (time_period_id) REFERENCES time_period (id);

ALTER TABLE contract_time_period DROP CONSTRAINT contract_time_period_contract_id_fkey;
ALTER TABLE contract_time_period
    ADD FOREIGN KEY (contract_id) REFERENCES contract (id);

ALTER TABLE contract_time_period DROP CONSTRAINT contract_time_period_time_period_id_fkey;
ALTER TABLE contract_time_period
    ADD FOREIGN KEY (time_period_id) REFERENCES time_period (id);

ALTER TABLE courier_region DROP CONSTRAINT courier_region_courier_id_fkey;
ALTER TABLE courier_region
    ADD FOREIGN KEY (courier_id) REFERENCES courier (id);

ALTER TABLE courier_region DROP CONSTRAINT courier_region_region_id_fkey;
ALTER TABLE courier_region
    ADD FOREIGN KEY (region_id) REFERENCES region (id);

-- delete column with primary key and add constraint composite primary keys

ALTER TABLE courier_region DROP COLUMN courier_region_id;
ALTER TABLE courier_region
    ADD PRIMARY KEY (courier_id, region_id);

ALTER TABLE courier_time_period DROP COLUMN courier_time_period_id;
ALTER TABLE courier_time_period
    ADD PRIMARY KEY (courier_id, time_period_id);

ALTER TABLE contract_time_period DROP COLUMN contract_time_period_id;
ALTER TABLE contract_time_period
    ADD PRIMARY KEY (contract_id, time_period_id);
