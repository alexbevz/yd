DELETE FROM courier_time_period;
DELETE FROM courier_region;
DELETE FROM order_time_period;
DELETE FROM "order";
DELETE FROM time_period;
DELETE FROM region;
DELETE FROM courier;

ALTER SEQUENCE time_period_time_period_id_seq RESTART WITH 1;
ALTER SEQUENCE region_region_id_seq RESTART WITH 1;