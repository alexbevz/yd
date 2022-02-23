ALTER TABLE "order" DROP CONSTRAINT order_courier_id_fkey;
ALTER TABLE "order" DROP CONSTRAINT order_type_courier_id_fkey;
ALTER TABLE "order" DROP COLUMN status;
ALTER TABLE "order" DROP COLUMN courier_id;
ALTER TABLE "order" DROP COLUMN datetime_realization;
ALTER TABLE "order" DROP COLUMN datetime_realization_start;
ALTER TABLE "order" DROP COLUMN datetime_assignment;
ALTER TABLE "order" DROP COLUMN type_courier_id;