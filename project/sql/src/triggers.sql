-- Triggers for database-messenger
-- Trigger and procedure for putting in USER_LIST list_id

-- Trigger and procedure for CHAT chat_id
CREATE OR REPLACE FUNCTION new_cid() RETURNS trigger AS $cid$
    BEGIN
        -- If the chat_id is null, then add in a number from the sequence
        IF NEW.chat_id IS NULL THEN
            NEW.chat_id := nextval('chat_chat_id_seq');
            RETURN NEW;
        END IF;
    END;
$cid$ LANGUAGE plpgsql;

CREATE TRIGGER cid BEFORE INSERT ON CHAT
    FOR EACH ROW EXECUTE PROCEDURE new_cid();
-- Trigger and procedure for MESSAGE msg_id and msg_timestamp


