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

   SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
   START TRANSACTION;

ActionHandler:BEGIN

DECLARE `var_passenger_id` BIGINT;
DECLARE `var_real_money` DECIMAL(20,2);
DECLARE `var_real_change` DECIMAL(20,2) DEFAULT 0;

INSERT INTO `wallet`.`test` (`msg`) VALUES ('a');

SELECT
`id`, `realMoney`
INTO
`var_passenger_id`, `var_real_money`
FROM
`wallet`.`passenger`
WHERE
id = `passenger_id`
LIMIT 1;


IF `var_passenger_id` IS NULL THEN
SET `status` = 2;
SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'passenger not found';
END IF;


IF `amount` > (`var_real_money`) THEN
SET `status` = 3;
SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient funds';
END IF;


IF `action` = 'refund' OR `action` = 'touchout' THEN
SET `var_real_change` = ABS(`amount`);
ELSE
IF `amount` <= (`var_real_money`) THEN
SET `var_real_change` = -1 * ABS(`amount`);
ELSE
SET `var_real_change` = -1 * `var_real_money`;
END IF;
END IF;

SET inbalance = var_real_money + `var_real_change`;

UPDATE
`wallet`.`passenger`
SET
`realMoney` = inbalance
WHERE `id` = `var_passenger_id`;

SET error = '';
SET status = 0;

COMMIT;

END;
END //

DELIMITER ;
