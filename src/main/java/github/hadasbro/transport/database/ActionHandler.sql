DELIMITER //
CREATE /* DEFINER=`root`@`localhost` */ PROCEDURE `ActionHandler`(
	IN `passenger_id` BIGINT,
	IN `amount` DOUBLE,
	IN `action` VARCHAR(50),
	OUT `error` VARCHAR(250),
	OUT `status` INT,
	INOUT `inbalance` DOUBLE
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLSTATE '42S22'
	BEGIN
        GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE, @errno = MYSQL_ERRNO, @error_text = MESSAGE_TEXT;
        SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @error_text);
        SET error = @full_error;
        ROLLBACK;
	END;

ActionHandler:BEGIN

    DECLARE `var_passenger_id` BIGINT;
    DECLARE `var_balance` DECIMAL(20,2);
    DECLARE `var_balance_change` DECIMAL(20,2) DEFAULT 0;


    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
    START TRANSACTION;

        SELECT
            `id`, `balance`
        INTO
            `var_passenger_id`, `var_balance`
        FROM
            `underground_api`.`passenger`
        WHERE
            id = `passenger_id`
        LIMIT 1
        FOR UPDATE;

        IF `var_passenger_id` IS NULL THEN
            SET `status` = 2;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Passenger not found';
        END IF;


        IF `amount` > (`var_balance`) AND `action` != 'refund' THEN
            SET `status` = 3;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient funds';
        END IF;


        IF `action` = 'refund' THEN
            SET `var_balance_change` = ABS(`amount`);
        ELSE
            SET `var_balance_change` = -1 * ABS(`amount`);
        END IF;

        SET `inbalance` = `var_balance` + `var_balance_change`;

        UPDATE
            `underground_api`.`passenger`
        SET
            `balance` = `inbalance`
        WHERE
            `id` = `var_passenger_id`;

        SET error = '';
        SET status = 0;

    COMMIT;

END;
END //
DELIMITER ;
