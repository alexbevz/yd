-- rename type status_contract to status_order
ALTER TYPE status_contract RENAME TO status_order;

-- rename table contract to order
ALTER TABLE contract
    RENAME TO "order";

-- rename constraint contract_pkey to order_pkey
ALTER TABLE "order"
    RENAME CONSTRAINT contract_pkey TO order_pkey;

-- rename constraint contract_region_id_fkey to order_region_id_fkey
ALTER TABLE "order"
    RENAME CONSTRAINT contract_region_id_fkey TO order_region_id_fkey;

-- rename constraint contract_courier_id_fkey to order_courier_id_fkey
ALTER TABLE "order"
    RENAME CONSTRAINT contract_courier_id_fkey TO order_courier_id_fkey;

-- rename constraint contract_type_courier_id_fkey to order_type_courier_id_fkey
ALTER TABLE "order"
    RENAME CONSTRAINT contract_type_courier_id_fkey TO order_type_courier_id_fkey;

-- rename constraint contract_weight_check to order_weight_check
ALTER TABLE "order"
    RENAME CONSTRAINT contract_weight_check TO order_weight_check;

-- alter type status_contract to status_order
ALTER TABLE "order"
    ALTER COLUMN status TYPE status_order;

-- rename table contract_time_period to order_time_period
ALTER TABLE contract_time_period
    RENAME TO order_time_period;

-- alter constraint for order_time_period
ALTER TABLE order_time_period
    DROP CONSTRAINT contract_time_period_pkey;
ALTER TABLE order_time_period
    DROP CONSTRAINT contract_time_period_contract_id_fkey;
ALTER TABLE order_time_period
    RENAME CONSTRAINT contract_time_period_time_period_id_fkey TO order_time_period_time_period_id_fkey;

-- rename column contract_id to order_id
ALTER TABLE order_time_period
    RENAME COLUMN contract_id TO order_id;

ALTER TABLE order_time_period
    ADD FOREIGN KEY (order_id) REFERENCES "order" (id);
ALTER TABLE order_time_period
    ADD PRIMARY KEY (order_id, time_period_id);
