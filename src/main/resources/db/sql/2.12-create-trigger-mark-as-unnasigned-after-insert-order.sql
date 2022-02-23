-- функция создания неназначенного заказа
CREATE FUNCTION create_unassigned_order()
    RETURNS trigger
    LANGUAGE plpgsql AS
'BEGIN
    INSERT INTO unassigned_order
    VALUES (NEW.id);
    RETURN NEW;
END';
-- функция создания неназначенного заказа

-- триггер при создании заказа
CREATE TRIGGER mark_as_unassigned
    AFTER INSERT
    ON "order"
    FOR EACH ROW
EXECUTE PROCEDURE create_unassigned_order();
-- триггер при создании заказа