-- rename type status_order to status_contract
ALTER TYPE status_order RENAME TO status_contract;

-- rename table order to contract
ALTER TABLE "order"
    RENAME TO contract;

-- rename constraint order_pkey to contract_pkey
ALTER TABLE contract
    RENAME CONSTRAINT order_pkey TO contract_pkey;

-- rename constraint order_region_id_fkey to contract_region_id_fkey
ALTER TABLE contract
    RENAME CONSTRAINT order_region_id_fkey TO contract_region_id_fkey;

-- rename constraint order_courier_id_fkey to contract_courier_id_fkey
ALTER TABLE contract
    RENAME CONSTRAINT order_courier_id_fkey TO contract_courier_id_fkey;

-- rename constraint order_type_courier_id_fkey to contract_type_courier_id_fkey
ALTER TABLE contract
    RENAME CONSTRAINT order_type_courier_id_fkey TO contract_type_courier_id_fkey;

-- rename constraint order_weight_check to contract_weight_check
ALTER TABLE contract
    RENAME CONSTRAINT order_weight_check TO contract_weight_check;

-- alter type status_order to status_contract
ALTER TABLE contract
    ALTER COLUMN status TYPE status_contract;

-- rename table order_time_period to contract_time_period
ALTER TABLE order_time_period
    RENAME TO contract_time_period;

-- alter constraint for order_time_period
ALTER TABLE contract_time_period
    DROP CONSTRAINT order_time_period_pkey;
ALTER TABLE contract_time_period
    DROP CONSTRAINT order_time_period_order_id_fkey;
ALTER TABLE contract_time_period
    RENAME CONSTRAINT order_time_period_time_period_id_fkey TO contract_time_period_time_period_id_fkey;

-- rename column order_id to contract_id
ALTER TABLE contract_time_period
    RENAME COLUMN order_id TO contract_id;

ALTER TABLE contract_time_period
    ADD FOREIGN KEY (contract_id) REFERENCES contract (id);
ALTER TABLE contract_time_period
    ADD PRIMARY KEY (contract_id, time_period_id);
