
ALTER TABLE contract ADD COLUMN type_courier_id int REFERENCES type_courier(id);
