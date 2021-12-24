-- static data
CREATE TYPE status_contract AS ENUM ('UNASSIGNED', 'ASSIGNED', 'COMPLETED');

CREATE TABLE type_courier
(
    type_courier_id serial PRIMARY KEY,
    name_type       varchar(10) UNIQUE NOT NULL,
    capacity        real               NOT NULL CHECK (
                capacity >= 0.01
            AND capacity <= 50
        ),
    profit_ratio    real               NOT NULL CHECK (profit_ratio > 0)
);

-- insert data
INSERT INTO type_courier (type_courier_id,
                          name_type,
                          capacity,
                          profit_ratio)
VALUES (1, 'foot', 10, 2),
       (2, 'bike', 15, 5),
       (3, 'car', 50, 9);

-- general entity
CREATE TABLE time_period
(
    time_period_id serial PRIMARY KEY,
    left_limit     time NOT NULL,
    right_limit    time NOT NULL
);

CREATE TABLE region
(
    region_id     serial PRIMARY KEY,
    number_region int NOT NULL UNIQUE CHECK (number_region > 0)
);

-- primary entity
CREATE TABLE courier
(
    courier_id      serial PRIMARY KEY,
    type_courier_id int REFERENCES type_courier (type_courier_id)
);

CREATE TABLE contract
(
    contract_id          serial PRIMARY KEY,
    region_id            int REFERENCES region (region_id),
    weight               real                                 NOT NULL CHECK (
                weight > 0.01
            AND weight <= 50
        ),
    status               status_contract DEFAULT 'UNASSIGNED' NOT NULL,
    courier_id           int             DEFAULT NULL REFERENCES courier (courier_id),
    datetime_assignment  timestamp       DEFAULT NULL,
    datetime_realization timestamp       DEFAULT NULL
);

-- secondary entity
CREATE TABLE courier_time_period
(
    courier_time_period_id serial PRIMARY KEY,
    courier_id             int NOT NULL REFERENCES courier (courier_id),
    time_period_id         int NOT NULL REFERENCES time_period (time_period_id)
);

CREATE TABLE contract_time_period
(
    contract_time_period_id serial PRIMARY KEY,
    contract_id             int NOT NULL REFERENCES contract (contract_id),
    time_period_id          int NOT NULL REFERENCES time_period (time_period_id)
);

CREATE TABLE courier_region
(
    courier_region_id serial PRIMARY KEY,
    courier_id        int NOT NULL REFERENCES courier (courier_id),
    region_id         int NOT NULL REFERENCES region (region_id)
);