
CREATE DATABASE IF NOT EXISTS `underground_api` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `underground_api`;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `ActionHandler`(
	IN `passenger_id` BIGINT,
	IN `amount` DOUBLE,
	IN `action` VARCHAR(50),
	OUT `error` VARCHAR(250),
	OUT `status` INT,
	INOUT `inbalance` DOUBLE
)
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
END//
DELIMITER ;

CREATE TABLE IF NOT EXISTS `api_logs` (
  `id` bigint(20) NOT NULL,
  `date` datetime DEFAULT NULL,
  `error` text,
  `request` text,
  `req_actionIdentifier` varchar(255) DEFAULT NULL,
  `req_closed` bit(1) DEFAULT NULL,
  `req_journeyidentifer` varchar(255) DEFAULT NULL,
  `req_journeylegIdentifer` varchar(255) DEFAULT NULL,
  `req_licenceId` int(11) DEFAULT NULL,
  `req_passengerId` bigint(20) DEFAULT NULL,
  `req_type` int(11) DEFAULT NULL,
  `response` text,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `api_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_logs` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `city` (
  `id` bigint(20) NOT NULL,
  `cityCode` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `country_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrpd7j1p7yxr784adkx4pyepba` (`country_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT IGNORE INTO `city` (`id`, `cityCode`, `name`, `country_id`) VALUES
	(66, 'CI17', 'City 17', 8),
	(67, 'CI7', 'City 7', 48),
	(68, 'CI16', 'City 16', 58),
	(69, 'CI0', 'City 0', 30),
	(70, 'CI8', 'City 8', 21),
	(71, 'CI11', 'City 11', 34),
	(72, 'CI19', 'City 19', 4),
	(73, 'CI1', 'City 1', 51),
	(74, 'CI9', 'City 9', 13),
	(75, 'CI10', 'City 10', 32),
	(76, 'CI18', 'City 18', 37),
	(77, 'CI2', 'City 2', 55),
	(78, 'CI13', 'City 13', 17),
	(79, 'CI3', 'City 3', 11),
	(80, 'CI12', 'City 12', 53),
	(81, 'CI4', 'City 4', 15),
	(82, 'CI15', 'City 15', 39),
	(83, 'CI5', 'City 5', 45),
	(84, 'CI14', 'City 14', 28),
	(85, 'CI6', 'City 6', 60);
/*!40000 ALTER TABLE `city` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `country` (
  `id` bigint(20) NOT NULL,
  `isoCode` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT IGNORE INTO `country` (`id`, `isoCode`, `name`) VALUES
	(4, 'CTR0', 'Country 0'),
	(8, 'CTR8', 'Country 8'),
	(11, 'CTR15', 'Country 15'),
	(13, 'CTR1', 'Country 1'),
	(15, 'CTR9', 'Country 9'),
	(17, 'CTR16', 'Country 16'),
	(21, 'CTR6', 'Country 6'),
	(28, 'CTR13', 'Country 13'),
	(30, 'CTR7', 'Country 7'),
	(32, 'CTR14', 'Country 14'),
	(34, 'CTR4', 'Country 4'),
	(37, 'CTR11', 'Country 11'),
	(39, 'CTR19', 'Country 19'),
	(45, 'CTR5', 'Country 5'),
	(48, 'CTR12', 'Country 12'),
	(51, 'CTR2', 'Country 2'),
	(53, 'CTR17', 'Country 17'),
	(55, 'CTR3', 'Country 3'),
	(58, 'CTR10', 'Country 10'),
	(60, 'CTR18', 'Country 18');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT IGNORE INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES
	(1, '1.0', 'init', 'SQL', 'V1.0__init.sql', 0, 'root', '2019-11-18 22:57:26', 2, 1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `general_logs` (
  `id` bigint(20) NOT NULL,
  `date` datetime DEFAULT NULL,
  `request` varchar(1000) DEFAULT NULL,
  `response` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `general_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `general_logs` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT IGNORE INTO `hibernate_sequence` (`next_val`) VALUES
	(306),
	(306),
	(306),
	(306),
	(306),
	(306),
	(306),
	(306),
	(306),
	(306),
	(306),
	(306);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `journey` (
  `id` bigint(20) NOT NULL,
  `dateStart` datetime DEFAULT NULL,
  `identifer` varchar(255) DEFAULT NULL,
  `lastAction` datetime DEFAULT NULL,
  `operator_id` bigint(20) NOT NULL,
  `passenger_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh4e4xyrfx6x8i739lf5a8s53i` (`operator_id`),
  KEY `FKq18vjmou3ytulqdvfad2wvyl7` (`passenger_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `journey` DISABLE KEYS */;
INSERT IGNORE INTO `journey` (`id`, `dateStart`, `identifer`, `lastAction`, `operator_id`, `passenger_id`) VALUES
	(156, '2019-11-18 23:09:09', 'gf7v1yv7gKvGqgwWTteHxXUYXDpcQG5f3Xuwtuxn5bKWcdpkJiquqwjP3ijN', '2019-11-18 23:09:09', 93, 145),
	(157, '2019-11-18 23:09:09', 'iXrirV7cifUM4OkhX6K7pQQurs4wejCiUsICIOKKOJOEm7XIN3tNbmI5ROV2', '2019-11-18 23:09:09', 94, 143),
	(158, '2019-11-18 23:09:09', 'RqEfXqoTQFmsUO7buoegFKlDDEWmT0GGKQ21ONgu6KK6CsCnV2ri1kixfnLG', '2019-11-18 23:09:09', 90, 110),
	(159, '2019-11-18 23:09:09', 'KeYx3PBkst0onFdreE8wdhieVjNxthbEwrtqy0quoVyDP4YWMRhSPtSrBMg6', '2019-11-18 23:09:09', 101, 121),
	(160, '2019-11-18 23:09:09', 'OlqW2MCVT76OnitG4MFXNVMMW10rVTjsyS0PglvRjJ2NgthLJ6Xdqr2j7NyR', '2019-11-18 23:09:09', 105, 125),
	(161, '2019-11-18 23:09:09', 'LesGfqw54wNd4cYBLFwV14NEDlKJLH8qL4ht5svbpw6W3e8KpQGUdGNP18cW', '2019-11-18 23:09:09', 90, 132),
	(162, '2019-11-18 23:09:09', 'yHhSMLN2DY6JTdJW7NgsoIVOUDn2qnStuDrnlVdeCgXEXekCnlhtfsiiRYBQ', '2019-11-18 23:09:09', 88, 128),
	(163, '2019-11-18 23:09:09', '3ou5NxsYHNJQiKrE4nqlD2VNmb0UqKBd611BFqPMBbOt7YUhPVlnVUF2OUpf', '2019-11-18 23:09:09', 98, 148),
	(164, '2019-11-18 23:09:09', 'iElebfqWiVDWOYvBW6RbRkGubBXS8ruIpnghEeiFlVojRqq220wETUCBnt05', '2019-11-18 23:09:09', 103, 138),
	(165, '2019-11-18 23:09:09', 'iM8gLHeDS4QEX7guK4sCuKiqkU6TmBK1gpfheiGKednoDQSO28S2WwpTj8eI', '2019-11-18 23:09:09', 99, 130),
	(166, '2019-11-18 23:09:09', 'smwsqOISUNf4cuoUtJPD6j6L3NPqdMlwFPilcKbX5fBGJJohREHsqlEJSqLY', '2019-11-18 23:09:09', 96, 147),
	(167, '2019-11-18 23:09:09', 'uTVJV2Hwn27QP8OECjwRLOGHrP4Dsqn1F0hkHkKCf1HgjeUmqSt52w8ULkoX', '2019-11-18 23:09:09', 103, 141),
	(168, '2019-11-18 23:09:09', 'YkKcmF0Cj8uwGgTY5OuN3HEiloDl3QbHNC67kSbFTOnl2XY7XQHlWQLX0yfV', '2019-11-18 23:09:09', 92, 136),
	(169, '2019-11-18 23:09:09', 'k2s2xQpPRcuposfyQvoBKDCJbLIDhSLoUYotO7DOSWRKVCuPKvq24gtr8GrN', '2019-11-18 23:09:09', 86, 149),
	(170, '2019-11-18 23:09:09', 'chEe4QUsrh4cNgvJxMcKBK4XVMc3Ep33hqj8vi2eejgHMXdDQUBcNcGNCNmy', '2019-11-18 23:09:09', 88, 153),
	(171, '2019-11-18 23:09:09', 'H8kLjpUpbsSdlFm0isW3m7M0rG3UQK3TLMxKOCLKTq6smIgHQiwse0WPXGKt', '2019-11-18 23:09:09', 92, 112),
	(172, '2019-11-18 23:09:09', 'HF6TrBKTWDCRcTPSj2h8QBTXEOHdI05bNJRUPkOo5YoFKWoqgghwVF4FoFIK', '2019-11-18 23:09:09', 88, 154),
	(173, '2019-11-18 23:09:09', 'ilOOwXNr3swueMiKJmPs1NwCMrGNNQ1wvFooCrKXlpFgEdNYCiMr4JsMRKVk', '2019-11-18 23:09:09', 100, 135),
	(174, '2019-11-18 23:09:09', '8k1YYfcqfjBdntgb6ulDB26grHrqgQoQSyle1Gkwe2yCVHRjxygwxV2s86eL', '2019-11-18 23:09:09', 87, 152),
	(175, '2019-11-18 23:09:09', 'ppXQYyP7UIlRWmfxcNkRt2IpE5ttkrICbXKrnurskOHW04kiSeqchn8sxhbH', '2019-11-18 23:09:09', 102, 122),
	(176, '2019-11-18 23:09:09', '648e5TFRl3kU3E6IIstO4L5rnvQLSl4qFuKpGp5fLnwX2cw87FDbs8N7gjml', '2019-11-18 23:09:09', 86, 106),
	(177, '2019-11-18 23:09:09', '2LdtX3XMy3YSC2lPhEN5UN8gkto1GPWjTrS7C6JDSkiPbHWlYSTl4wqBSypi', '2019-11-18 23:09:09', 99, 119),
	(178, '2019-11-18 23:09:09', 'ibbTCJ3XmhmiDUhTRse1iUVpP1swHf4PNkctIQeQc3hC23CRF4mXcrkWYwkD', '2019-11-18 23:09:09', 97, 142),
	(179, '2019-11-18 23:09:09', 'ETjRU1tw7er0fTrScEc8Pleo6cyWtqn4j0GYpp33H1QESsqSdEnnbrepmthC', '2019-11-18 23:09:09', 92, 126),
	(180, '2019-11-18 23:09:09', '35FSRxnm2pliF2xp6F62xeJcn7rmubxyHfhe2pkDe7J6QynnCoihUwX8P3eB', '2019-11-18 23:09:09', 87, 129),
	(181, '2019-11-18 23:09:09', 'mDus0bL6xR2KUI87nXYuDMEVQir8HwgqKqXUnQdDeVcdLB4FLBWnUqNGCN4y', '2019-11-18 23:09:09', 104, 131),
	(182, '2019-11-18 23:09:09', 'KK3nRjPdIsbdcO6koIJfxspOHMimJSbpslUc5qqeCGE358xQtrFdnQW5rM3n', '2019-11-18 23:09:09', 95, 137),
	(183, '2019-11-18 23:09:09', 'jBiLYnW2uw6C8eb8MWn6KbMIOc58JSmBEyteFOCH5WKBRgpNvNGNrODBRv7S', '2019-11-18 23:09:09', 105, 146),
	(184, '2019-11-18 23:09:09', '82VtFbkEcMC0cOfyiSrS1yEX3uyhcOWbHTbeBlvpXj2FNL4rdjUcMYyjml77', '2019-11-18 23:09:09', 96, 116),
	(185, '2019-11-18 23:09:09', 'xllmJLhMEuvIl5do4n1eXtolf6gE1FHVeSCcJr37OtqifIwrhNkg86VDOFbF', '2019-11-18 23:09:09', 97, 117),
	(186, '2019-11-18 23:09:09', 'DPUvv1H3h1CuxnpGGO20VOlIuh4tPb3yPfTjKiIFiwBpNLNkiBqJf4WhDe40', '2019-11-18 23:09:09', 94, 127),
	(187, '2019-11-18 23:09:09', 'StptFeHsyYHRf4lhYyY8lOC1k8Rw6Y61n26YHPbc7gcbSQSVuUhvFYeIPQV7', '2019-11-18 23:09:09', 91, 155),
	(188, '2019-11-18 23:09:09', 'bdvPe0olQlTxLWMIURykytUSuQ4vt6Lohg3jMyNGuQrXij1gvC38nyrhhMDv', '2019-11-18 23:09:09', 93, 113),
	(189, '2019-11-18 23:09:09', 'wwHeDuCC0bj6IlP63WCHLorWJ6heWetYMiit3CinjXU3KJxk7tXF2iPNLVN6', '2019-11-18 23:09:09', 101, 133),
	(190, '2019-11-18 23:09:09', 'ue5pGjnNOTv0QdJhHkrU2Ny1dgtlYeGoDyf4EqeYjXi2qG531SRLIRf34ux5', '2019-11-18 23:09:09', 89, 109),
	(191, '2019-11-18 23:09:09', 'UFBoTQ7Evi5EB2pnB6ExJJgSCl6qln8nHXV63qINyqiwig8nxBftSVGHedkq', '2019-11-18 23:09:09', 95, 115),
	(192, '2019-11-18 23:09:09', 'ygDm8X36WPGNqgh0CVhJYi0Y0bVij16NeMfCxQ5L4Ihmjgh7yUHcueddXguC', '2019-11-18 23:09:09', 105, 144),
	(193, '2019-11-18 23:09:09', 'EsCknenmkPOXnIq3OHJdMsyVJtiWnyRtkdyLpfV0qgo7rvQBM48yS6qLr5KS', '2019-11-18 23:09:09', 100, 134),
	(194, '2019-11-18 23:09:09', 'r2x40Ru3ofM1mnHk074ti17tgThJLVHWUQbtkcinHwlnRufEXo3yvCQK7Hf8', '2019-11-18 23:09:09', 96, 140),
	(195, '2019-11-18 23:09:09', 'QPrNBLU4eyPc5lh2vbVCjkYpOSkTX2qqVOGGBLYGTb8GnTPDxon3Gw8UlMGx', '2019-11-18 23:09:09', 91, 111),
	(196, '2019-11-18 23:09:09', 'SsHPVI7pPmLkGGopGc4ljLUEFkDp2dqK7S1fRhMrvJpkIthlVD4xhF3ie3w6', '2019-11-18 23:09:09', 87, 107),
	(197, '2019-11-18 23:09:09', '8CcBmoLXNJYMEhhNfF1XDcR72xGiUKs1rRdw1CBEuDYjVEGBuXO8N4UBYQKR', '2019-11-18 23:09:09', 100, 120),
	(198, '2019-11-18 23:09:09', 'OYJJtgrpeeUUFy1Hqgo7RMK5eH8ULv8Kikrcdcgdgh475nom4hwR6qy0wrKh', '2019-11-18 23:09:09', 91, 151),
	(199, '2019-11-18 23:09:09', '6ntyQEvEfIC5ynDD8UKneTddmy2P888vQw3QdndSnxLJtT3KveNCBK1KSRgb', '2019-11-18 23:09:09', 104, 124),
	(200, '2019-11-18 23:09:09', 'f5EgBUJRm7TVuMKmiX5Q3SPPJWCcXuP0OekJr0GpLW7mSGFLkgCjWkIF0PL3', '2019-11-18 23:09:09', 94, 114),
	(201, '2019-11-18 23:09:09', '7RiTQ5SlI5KkvlGgiuWm3KJpWcQIo5lBL2JudIeTDi3x72uvEdtnn0lyulwi', '2019-11-18 23:09:09', 98, 118),
	(202, '2019-11-18 23:09:09', 'xUngoHRN6BIHcSbCx5GhVD8b7VQlJnbtoDcuLT4G6BupLo787MmGgLIUBFxI', '2019-11-18 23:09:09', 105, 139),
	(203, '2019-11-18 23:09:09', 'xPO4bsc581YbWOyN3bBBTikOoblvWDLX3HrHbWoTLkJ4QWIO780wK10rOdYG', '2019-11-18 23:09:09', 88, 108),
	(204, '2019-11-18 23:09:09', 'NePBXrc7KQGnmF1TFxci3lDvsrLoI4eBrScJf3vFD8FvrN8Nmlkvv2qWtxRq', '2019-11-18 23:09:09', 101, 150),
	(205, '2019-11-18 23:09:09', 't4Du38R0IysDURV3YrtLvpGEmkYbEGdclRSoeim5OJLCr82s6OUu0Lq3Jpgl', '2019-11-18 23:09:09', 103, 123);
/*!40000 ALTER TABLE `journey` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `journeyleg` (
  `id` bigint(20) NOT NULL,
  `closed` bit(1) NOT NULL,
  `firstAction` datetime DEFAULT NULL,
  `identifer` varchar(255) DEFAULT NULL,
  `lastAction` datetime DEFAULT NULL,
  `journey_id` bigint(20) NOT NULL,
  `operator_id` bigint(20) NOT NULL,
  `passenger_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKne1u149mxmcs8dngk69ncu9nk` (`journey_id`),
  KEY `FKsq16ab5s72xsdfqirfaunlmo2` (`operator_id`),
  KEY `FKsiwcwj1fywytb7u2o7b25b921` (`passenger_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `journeyleg` DISABLE KEYS */;
INSERT IGNORE INTO `journeyleg` (`id`, `closed`, `firstAction`, `identifer`, `lastAction`, `journey_id`, `operator_id`, `passenger_id`) VALUES
	(206, b'0', '2019-11-18 23:09:09', 'TFepQUJ0e8orXhwM8CLG32g2N8yVIoh82CVf8Lj2ePbv83XUrIDjoV8pfiDy', '2019-11-18 23:09:09', 163, 93, 113),
	(207, b'0', '2019-11-18 23:09:09', '8Ol11Dycdc6SBNeM1ODOFCdn1i0xxQSpvmPRCIleuhEPGiSFXYD7PRRXNNpB', '2019-11-18 23:09:09', 205, 91, 155),
	(208, b'0', '2019-11-18 23:09:09', 'GyRH0mWvgHDgRsUWeTFh7sWVRhUdvJfhCXJytpCcj3GqN4MN0FQh6uKmDrpI', '2019-11-18 23:09:09', 162, 92, 112),
	(209, b'0', '2019-11-18 23:09:09', '8DCnP4UvJvBP5RPxkkoqOo637wllNktcNNNTUC44I5mSKtCBdvDi8BuhTWeh', '2019-11-18 23:09:09', 175, 105, 125),
	(210, b'0', '2019-11-18 23:09:09', 'rv8tPRQ8uhf5ffEoTlvuEXVq2StYGFIo1kFUcmgyWnHs23qx7H0s4mbbGcBt', '2019-11-18 23:09:09', 172, 102, 122),
	(211, b'0', '2019-11-18 23:09:09', '63I6FgdB3OmdfCwXmuUL6MgNjLGVUEMIggX3ctUnqo07mvv80kQBr1jcWgsg', '2019-11-18 23:09:09', 182, 99, 132),
	(212, b'0', '2019-11-18 23:09:09', 'fVbPgT12tI5eUdf3HKtXXnfHNOHsqJrU5UJtcLO8HGBnwflO2dYR6QuS2uhs', '2019-11-18 23:09:09', 168, 98, 118),
	(213, b'0', '2019-11-18 23:09:09', 'Vnfu460HovB1qXqE4IwduRYQpsyMwmfwOqUbcVMriQMxswi6nvcvhXb6oKjd', '2019-11-18 23:09:09', 174, 104, 124),
	(214, b'0', '2019-11-18 23:09:09', 'qET5cEoljBMmy73ytHMCUgL00XBJBir6LJyUHwJFpIrXwbYbF8P1SH21L8mY', '2019-11-18 23:09:09', 167, 97, 117),
	(215, b'0', '2019-11-18 23:09:09', 'Kyu7HEHlqq8DdloJPMSSs5mG7ElOycVi1pisvu5DKun8fukB2gqJgeD7SB1w', '2019-11-18 23:09:09', 188, 87, 138),
	(216, b'0', '2019-11-18 23:09:09', 'O7BnJKf4wXR8NFXBXefOpU7lklMpV1eDb42Q7vHInlNW46Ho6d8veiRxj3Ko', '2019-11-18 23:09:09', 195, 102, 145),
	(217, b'0', '2019-11-18 23:09:09', 'l4sXiTykryP5m1D4hUfo3xL7uKVtNveoxgc2N215OYsWq3rMmDS6Trgl1VYy', '2019-11-18 23:09:09', 183, 94, 133),
	(218, b'0', '2019-11-18 23:09:09', 'CtehOR5W16IpryYK7v2DLYTuCosJSFhkFrdIji2bBgLJyxW1PXbEPu7x1xEc', '2019-11-18 23:09:09', 173, 103, 123),
	(219, b'0', '2019-11-18 23:09:09', 'GgJidX38YoUqUHoJcugURsMUrieSnc2ECvuNOYIGywqf1mIrJofQeiwQmI0M', '2019-11-18 23:09:09', 184, 90, 134),
	(220, b'0', '2019-11-18 23:09:09', 'DdLLstovI7PJjHFhB1vycXuqS2NP1754KigGsDPMM8ErYXfJQtU4oTtLOx54', '2019-11-18 23:09:09', 165, 95, 115),
	(221, b'0', '2019-11-18 23:09:09', 'kvrMerKd50gXcRYt20IxM1QXYYendlhiXxoEHWd6d65LwiKoI3BykU7rGxqi', '2019-11-18 23:09:09', 180, 87, 130),
	(222, b'0', '2019-11-18 23:09:09', 'DRODjXdYVQWYqHBMUqDqiOlSNe0sfLqmncTpCeixy58mpF2VORMEltOgNEDY', '2019-11-18 23:09:09', 200, 94, 150),
	(223, b'0', '2019-11-18 23:09:09', 'nXFcFPsBr6rrinUyoENLxHhyjV8eqqkDINn5JK024IiBoUDDwNfwgTftkvbC', '2019-11-18 23:09:09', 158, 88, 108),
	(224, b'0', '2019-11-18 23:09:09', 'TFvwI5eLTHWCvoRtO7pvvBg5ggIwC04EPbsdBVWLelq4hwVpsVric6lYiPWd', '2019-11-18 23:09:09', 159, 89, 109),
	(225, b'0', '2019-11-18 23:09:09', 'CbsDdsYmnR817BmSC8lS287YicwhL5GwFkoY6ML1yJxBhufBGc0d0LBtdYv0', '2019-11-18 23:09:09', 178, 87, 128),
	(226, b'0', '2019-11-18 23:09:09', 'vJEuXQbv68Uqob2C1yiiKmvPmyOrk8LURqErT83JbDFOsih251RNiX8dhPmK', '2019-11-18 23:09:09', 160, 90, 110),
	(227, b'0', '2019-11-18 23:09:09', 'Eehl6kE3E1oxJmXPFKJiL1U6uI4w1UJiG3ERpMFvCgeqrlQkImJGjt2OYGXs', '2019-11-18 23:09:09', 157, 87, 107),
	(228, b'0', '2019-11-18 23:09:09', 'yfk8WhkKjghJgDHJ1tthUjU8wXIyMIFbRyNVi7Dvkk0iwmVPwhQjTQj5WCkT', '2019-11-18 23:09:09', 181, 89, 131),
	(229, b'0', '2019-11-18 23:09:09', 'GRPXnbPjhCsR7XYerd7pL5yQi8LW440RbDvIOUOTtXBnGyKtBMWdG1pKlWs6', '2019-11-18 23:09:09', 179, 102, 129),
	(230, b'0', '2019-11-18 23:09:09', 'dYhoPBtEU7sP86lTxNHyYSXtW7sYqJBETIv8fBVR7JX4vBXKH7OCeYxr2PuO', '2019-11-18 23:09:09', 164, 94, 114),
	(231, b'0', '2019-11-18 23:09:09', 'sVEfpKSe0yVoCMfiS0H5BJuvbfTegesp6tlM1FVYeqtWBPuLVWuVrGhbnSnr', '2019-11-18 23:09:09', 202, 95, 152),
	(232, b'0', '2019-11-18 23:09:09', 'pO5u3hb17qs6YtD1QQi4Nixy6HdugLCj1jdvg4sWBdbFbrquWCFrq5Db8YhO', '2019-11-18 23:09:09', 166, 96, 116),
	(233, b'0', '2019-11-18 23:09:09', 'wv6E5EMp1gjx2hHBGnQqyiuwMCvbDtU3Qkce5siO4duOUVYx7tXqh5fd6YDU', '2019-11-18 23:09:09', 169, 99, 119),
	(234, b'0', '2019-11-18 23:09:09', 'YpW1BJnxtynIxwNxKf4LML7wftV5rxnQ0yxHOruldIL2Q55RI1k5Lh4pgGcd', '2019-11-18 23:09:09', 177, 86, 127),
	(235, b'0', '2019-11-18 23:09:09', 'Kj5P7Onk1yYMVRWjGmUqQg2d6ELMGYunBKtv6nomj5nWHXFRgrpIqeB6MyrJ', '2019-11-18 23:09:09', 198, 90, 148),
	(236, b'0', '2019-11-18 23:09:09', 'QYF7Vb4OmoIcFJ1JD0RvVOTFjiLHdrOukyQD31piNfsnYNsn1VBTT0pbC44M', '2019-11-18 23:09:09', 156, 86, 106),
	(237, b'0', '2019-11-18 23:09:09', 'vJCwQDWOFnERMwuG4Pp54Sx36W5RpiNdWF35qh4MX6BbnsMq6MBM4nH38wO3', '2019-11-18 23:09:09', 161, 91, 111),
	(238, b'0', '2019-11-18 23:09:09', 'yE4Ni76h7jjsNyWCNbXv4P8LQQFQ34Ot47KqNm78swm1i0gXBPBK6j47DCgP', '2019-11-18 23:09:09', 185, 102, 135),
	(239, b'0', '2019-11-18 23:09:09', 'YUi6DWkuf1JYsGdWIS3EoQp0ueK6ohSSjQ3y3TcbqDT2FwD26wwC1Rf2cUKO', '2019-11-18 23:09:09', 187, 93, 137),
	(240, b'0', '2019-11-18 23:09:09', 'e18Xq2sVF6mm5s7R3jc3VHIXqKB6ogmRigSjJVXp1CgM81DCqduihrML4Yr0', '2019-11-18 23:09:09', 199, 86, 149),
	(241, b'0', '2019-11-18 23:09:09', 'bocEDRFv7JsR6jqWHYK8XmHGdwpOQgNQsnNi0d4f34MTFXkr5CkRLNjlU01p', '2019-11-18 23:09:09', 171, 101, 121),
	(242, b'0', '2019-11-18 23:09:09', 'XTxRvy5X8yoVfuprs85LDq2bMd0gSP1ktIryDgb2k3ebeMvdNntEnG4NB0Lb', '2019-11-18 23:09:09', 190, 100, 140),
	(243, b'0', '2019-11-18 23:09:09', 'E7Ub58Kj3f7U0uqL6Lr8Tuxl1b5JrTsifM06ioxNVfeCLFntOcgnVnDmvLO5', '2019-11-18 23:09:09', 170, 100, 120),
	(244, b'0', '2019-11-18 23:09:09', 'K2CfRH7Jrv4xGT56Cqk5QscnrMDMCShryjvHO4HShp6Olfc6riqjBB7hcIo3', '2019-11-18 23:09:09', 189, 102, 139),
	(245, b'0', '2019-11-18 23:09:09', 'Ktt3NI1uBlT2ILgih2nSrRlen4igwjsBqF2JUwPlY3d68qTCk0NboyT6iU7l', '2019-11-18 23:09:09', 192, 102, 142),
	(246, b'0', '2019-11-18 23:09:09', 'KomLOQpln4JST610NEsmbIFLYimg2KD6s3IKLngr53Pw582W544j1DqW0CQu', '2019-11-18 23:09:09', 197, 104, 147),
	(247, b'0', '2019-11-18 23:09:09', 'SEoLPOvrxG8BKnVnnGQK7M2rwOSdyVwLMdKyrMQmQUiYn65KOOl7WUeb7e87', '2019-11-18 23:09:09', 191, 99, 141),
	(248, b'0', '2019-11-18 23:09:09', 'IU6nVOKm4pIW4E1Hw0UqmbJij68rOuOKlOFeT5ytqHoiOedb2YbgWv8s0Ky1', '2019-11-18 23:09:09', 186, 99, 136),
	(249, b'0', '2019-11-18 23:09:09', 'SgGs4dXNoNCHGV0HWGYh054bmTUYrS1FE2KdO2BhyR3frqNiyGwgkqMQTwFV', '2019-11-18 23:09:09', 201, 95, 151),
	(250, b'0', '2019-11-18 23:09:09', '5TG6T5MOBkNpum5iFFiHDtYCYB8YlIx7jOc8yRg15RXv2QHMyGCbir7Q2nlY', '2019-11-18 23:09:09', 203, 89, 153),
	(251, b'0', '2019-11-18 23:09:09', '3NWcTdt8LIqoiOmfhwFqHsDyFST0EmLqoiuMESEJWvnTSFOPi3JTPfYisI2X', '2019-11-18 23:09:09', 194, 99, 144),
	(252, b'0', '2019-11-18 23:09:09', 'CCYp8tLmLhVuG0rlthnRnFh8X5LSBo6xSIuRYEE2fepI2f1UoeY0WsQnmHWr', '2019-11-18 23:09:09', 204, 96, 154),
	(253, b'0', '2019-11-18 23:09:09', '2dc3spckm7VJ8myGOBjvQ0WMo4dXXWgb3gTpyd5pjvGC1FeeVh2hnmV0ENdp', '2019-11-18 23:09:09', 176, 90, 126),
	(254, b'0', '2019-11-18 23:09:09', 'S0oX2oHJyLopwm8s8yeeGkPSNwIflouWV6jU30ytU5Q2l7bLEW5EG14weIrh', '2019-11-18 23:09:09', 193, 99, 143),
	(255, b'0', '2019-11-18 23:09:09', 'GL2erW8NldsC3DPd18CPTxcjHFM3BoLeCrO46PYq60yc1yfnGskLliU6NGQ8', '2019-11-18 23:09:09', 196, 105, 146);
/*!40000 ALTER TABLE `journeyleg` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `operator` (
  `id` bigint(20) NOT NULL,
  `licenceId` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `operatorCode` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfbhatuqhgeoqwts3bscwmwik0` (`owner_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `operator` DISABLE KEYS */;
INSERT IGNORE INTO `operator` (`id`, `licenceId`, `name`, `operatorCode`, `status`, `type`, `owner_id`) VALUES
	(86, 5004, 'Operator #4', 'operator-errorCode-4', 2, 3, 14),
	(87, 5018, 'Operator #18', 'operator-errorCode-18', 3, 1, 64),
	(88, 5009, 'Operator #9', 'operator-errorCode-9', 1, 1, 43),
	(89, 5015, 'Operator #15', 'operator-errorCode-15', 1, 3, 61),
	(90, 5011, 'Operator #11', 'operator-errorCode-11', 1, 3, 49),
	(91, 5013, 'Operator #13', 'operator-errorCode-13', 1, 3, 56),
	(92, 5000, 'Operator #0', 'operator-errorCode-0', 3, 1, 2),
	(93, 5007, 'Operator #7', 'operator-errorCode-7', 1, 1, 26),
	(94, 5010, 'Operator #10', 'operator-errorCode-10', 1, 3, 46),
	(95, 5016, 'Operator #16', 'operator-errorCode-16', 2, 1, 62),
	(96, 5014, 'Operator #14', 'operator-errorCode-14', 1, 3, 59),
	(97, 5001, 'Operator #1', 'operator-errorCode-1', 2, 3, 5),
	(98, 5019, 'Operator #19', 'operator-errorCode-19', 2, 1, 65),
	(99, 5003, 'Operator #3', 'operator-errorCode-3', 1, 2, 10),
	(100, 5008, 'Operator #8', 'operator-errorCode-8', 3, 2, 35),
	(101, 5017, 'Operator #17', 'operator-errorCode-17', 1, 1, 63),
	(102, 5006, 'Operator #6', 'operator-errorCode-6', 2, 2, 23),
	(103, 5005, 'Operator #5', 'operator-errorCode-5', 2, 1, 19),
	(104, 5002, 'Operator #2', 'operator-errorCode-2', 1, 2, 7),
	(105, 5012, 'Operator #12', 'operator-errorCode-12', 1, 3, 52);
/*!40000 ALTER TABLE `operator` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `operator_city` (
  `operator_id` bigint(20) NOT NULL,
  `city_id` bigint(20) NOT NULL,
  PRIMARY KEY (`operator_id`,`city_id`),
  KEY `FK2w8hd9myou8tqphrb02rgbmr2` (`city_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `operator_city` DISABLE KEYS */;
INSERT IGNORE INTO `operator_city` (`operator_id`, `city_id`) VALUES
	(86, 66),
	(86, 67),
	(86, 68),
	(86, 69),
	(86, 70),
	(86, 71),
	(86, 72),
	(86, 73),
	(86, 74),
	(86, 75),
	(86, 76),
	(86, 77),
	(86, 78),
	(86, 79),
	(86, 80),
	(86, 81),
	(86, 82),
	(86, 83),
	(86, 84),
	(86, 85),
	(87, 66),
	(87, 67),
	(87, 68),
	(87, 69),
	(87, 70),
	(87, 71),
	(87, 72),
	(87, 73),
	(87, 74),
	(87, 75),
	(87, 76),
	(87, 77),
	(87, 78),
	(87, 79),
	(87, 80),
	(87, 81),
	(87, 82),
	(87, 83),
	(87, 84),
	(87, 85),
	(88, 66),
	(88, 67),
	(88, 68),
	(88, 69),
	(88, 70),
	(88, 71),
	(88, 72),
	(88, 73),
	(88, 74),
	(88, 75),
	(88, 76),
	(88, 77),
	(88, 78),
	(88, 79),
	(88, 80),
	(88, 81),
	(88, 82),
	(88, 83),
	(88, 84),
	(88, 85),
	(89, 66),
	(89, 67),
	(89, 68),
	(89, 69),
	(89, 70),
	(89, 71),
	(89, 72),
	(89, 73),
	(89, 74),
	(89, 75),
	(89, 76),
	(89, 77),
	(89, 78),
	(89, 79),
	(89, 80),
	(89, 81),
	(89, 82),
	(89, 83),
	(89, 84),
	(89, 85),
	(90, 66),
	(90, 67),
	(90, 68),
	(90, 69),
	(90, 70),
	(90, 71),
	(90, 72),
	(90, 73),
	(90, 74),
	(90, 75),
	(90, 76),
	(90, 77),
	(90, 78),
	(90, 79),
	(90, 80),
	(90, 81),
	(90, 82),
	(90, 83),
	(90, 84),
	(90, 85),
	(91, 66),
	(91, 67),
	(91, 68),
	(91, 69),
	(91, 70),
	(91, 71),
	(91, 72),
	(91, 73),
	(91, 74),
	(91, 75),
	(91, 76),
	(91, 77),
	(91, 78),
	(91, 79),
	(91, 80),
	(91, 81),
	(91, 82),
	(91, 83),
	(91, 84),
	(91, 85),
	(92, 66),
	(92, 67),
	(92, 68),
	(92, 69),
	(92, 70),
	(92, 71),
	(92, 72),
	(92, 73),
	(92, 74),
	(92, 75),
	(92, 76),
	(92, 77),
	(92, 78),
	(92, 79),
	(92, 80),
	(92, 81),
	(92, 82),
	(92, 83),
	(92, 84),
	(92, 85),
	(93, 66),
	(93, 67),
	(93, 68),
	(93, 69),
	(93, 70),
	(93, 71),
	(93, 72),
	(93, 73),
	(93, 74),
	(93, 75),
	(93, 76),
	(93, 77),
	(93, 78),
	(93, 79),
	(93, 80),
	(93, 81),
	(93, 82),
	(93, 83),
	(93, 84),
	(93, 85),
	(94, 66),
	(94, 67),
	(94, 68),
	(94, 69),
	(94, 70),
	(94, 71),
	(94, 72),
	(94, 73),
	(94, 74),
	(94, 75),
	(94, 76),
	(94, 77),
	(94, 78),
	(94, 79),
	(94, 80),
	(94, 81),
	(94, 82),
	(94, 83),
	(94, 84),
	(94, 85),
	(95, 66),
	(95, 67),
	(95, 68),
	(95, 69),
	(95, 70),
	(95, 71),
	(95, 72),
	(95, 73),
	(95, 74),
	(95, 75),
	(95, 76),
	(95, 77),
	(95, 78),
	(95, 79),
	(95, 80),
	(95, 81),
	(95, 82),
	(95, 83),
	(95, 84),
	(95, 85),
	(96, 66),
	(96, 67),
	(96, 68),
	(96, 69),
	(96, 70),
	(96, 71),
	(96, 72),
	(96, 73),
	(96, 74),
	(96, 75),
	(96, 76),
	(96, 77),
	(96, 78),
	(96, 79),
	(96, 80),
	(96, 81),
	(96, 82),
	(96, 83),
	(96, 84),
	(96, 85),
	(97, 66),
	(97, 67),
	(97, 68),
	(97, 69),
	(97, 70),
	(97, 71),
	(97, 72),
	(97, 73),
	(97, 74),
	(97, 75),
	(97, 76),
	(97, 77),
	(97, 78),
	(97, 79),
	(97, 80),
	(97, 81),
	(97, 82),
	(97, 83),
	(97, 84),
	(97, 85),
	(98, 66),
	(98, 67),
	(98, 68),
	(98, 69),
	(98, 70),
	(98, 71),
	(98, 72),
	(98, 73),
	(98, 74),
	(98, 75),
	(98, 76),
	(98, 77),
	(98, 78),
	(98, 79),
	(98, 80),
	(98, 81),
	(98, 82),
	(98, 83),
	(98, 84),
	(98, 85),
	(99, 66),
	(99, 67),
	(99, 68),
	(99, 69),
	(99, 70),
	(99, 71),
	(99, 72),
	(99, 73),
	(99, 74),
	(99, 75),
	(99, 76),
	(99, 77),
	(99, 78),
	(99, 79),
	(99, 80),
	(99, 81),
	(99, 82),
	(99, 83),
	(99, 84),
	(99, 85),
	(100, 66),
	(100, 67),
	(100, 68),
	(100, 69),
	(100, 70),
	(100, 71),
	(100, 72),
	(100, 73),
	(100, 74),
	(100, 75),
	(100, 76),
	(100, 77),
	(100, 78),
	(100, 79),
	(100, 80),
	(100, 81),
	(100, 82),
	(100, 83),
	(100, 84),
	(100, 85),
	(101, 66),
	(101, 67),
	(101, 68),
	(101, 69),
	(101, 70),
	(101, 71),
	(101, 72),
	(101, 73),
	(101, 74),
	(101, 75),
	(101, 76),
	(101, 77),
	(101, 78),
	(101, 79),
	(101, 80),
	(101, 81),
	(101, 82),
	(101, 83),
	(101, 84),
	(101, 85),
	(102, 66),
	(102, 67),
	(102, 68),
	(102, 69),
	(102, 70),
	(102, 71),
	(102, 72),
	(102, 73),
	(102, 74),
	(102, 75),
	(102, 76),
	(102, 77),
	(102, 78),
	(102, 79),
	(102, 80),
	(102, 81),
	(102, 82),
	(102, 83),
	(102, 84),
	(102, 85),
	(103, 66),
	(103, 67),
	(103, 68),
	(103, 69),
	(103, 70),
	(103, 71),
	(103, 72),
	(103, 73),
	(103, 74),
	(103, 75),
	(103, 76),
	(103, 77),
	(103, 78),
	(103, 79),
	(103, 80),
	(103, 81),
	(103, 82),
	(103, 83),
	(103, 84),
	(103, 85),
	(104, 66),
	(104, 67),
	(104, 68),
	(104, 69),
	(104, 70),
	(104, 71),
	(104, 72),
	(104, 73),
	(104, 74),
	(104, 75),
	(104, 76),
	(104, 77),
	(104, 78),
	(104, 79),
	(104, 80),
	(104, 81),
	(104, 82),
	(104, 83),
	(104, 84),
	(104, 85),
	(105, 66),
	(105, 67),
	(105, 68),
	(105, 69),
	(105, 70),
	(105, 71),
	(105, 72),
	(105, 73),
	(105, 74),
	(105, 75),
	(105, 76),
	(105, 77),
	(105, 78),
	(105, 79),
	(105, 80),
	(105, 81),
	(105, 82),
	(105, 83),
	(105, 84),
	(105, 85);
/*!40000 ALTER TABLE `operator_city` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `operator_vehicle` (
  `operator_id` bigint(20) NOT NULL,
  `vehicle_id` bigint(20) NOT NULL,
  PRIMARY KEY (`operator_id`,`vehicle_id`),
  KEY `FKpa8j65fr4pvm80vrkl2fsb5p1` (`vehicle_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `operator_vehicle` DISABLE KEYS */;
INSERT IGNORE INTO `operator_vehicle` (`operator_id`, `vehicle_id`) VALUES
	(86, 6),
	(86, 9),
	(86, 12),
	(87, 6),
	(87, 22),
	(88, 6),
	(89, 3),
	(89, 9),
	(89, 12),
	(91, 3),
	(91, 12),
	(92, 6),
	(92, 22),
	(93, 3),
	(93, 12),
	(93, 22),
	(94, 3),
	(94, 9),
	(94, 22),
	(95, 22),
	(96, 12),
	(97, 3),
	(97, 9),
	(98, 22),
	(99, 6),
	(99, 22),
	(100, 3),
	(100, 9),
	(100, 12),
	(101, 6),
	(101, 9),
	(102, 9),
	(102, 12),
	(104, 6),
	(105, 9),
	(105, 22);
/*!40000 ALTER TABLE `operator_vehicle` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `owner` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `slug` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `owner` DISABLE KEYS */;
INSERT IGNORE INTO `owner` (`id`, `name`, `slug`) VALUES
	(2, 'Owner 6', 'owner-slug-6'),
	(5, 'Owner 16', 'owner-slug-16'),
	(7, 'Owner 7', 'owner-slug-7'),
	(10, 'Owner 15', 'owner-slug-15'),
	(14, 'Owner 4', 'owner-slug-4'),
	(19, 'Owner 10', 'owner-slug-10'),
	(23, 'Owner 18', 'owner-slug-18'),
	(26, 'Owner 5', 'owner-slug-5'),
	(35, 'Owner 17', 'owner-slug-17'),
	(43, 'Owner 2', 'owner-slug-2'),
	(46, 'Owner 12', 'owner-slug-12'),
	(49, 'Owner 3', 'owner-slug-3'),
	(52, 'Owner 11', 'owner-slug-11'),
	(56, 'Owner 19', 'owner-slug-19'),
	(59, 'Owner 0', 'owner-slug-0'),
	(61, 'Owner 8', 'owner-slug-8'),
	(62, 'Owner 14', 'owner-slug-14'),
	(63, 'Owner 1', 'owner-slug-1'),
	(64, 'Owner 9', 'owner-slug-9'),
	(65, 'Owner 13', 'owner-slug-13');
/*!40000 ALTER TABLE `owner` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `passenger` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `balance` decimal(19,2) DEFAULT NULL,
  `blocked` bit(1) NOT NULL,
  `acId` int(11) NOT NULL,
  `activationDate` datetime DEFAULT NULL,
  `validityDate` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `city_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdl343iqf97f7k68vl44oo7htk` (`city_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `passenger` DISABLE KEYS */;
INSERT IGNORE INTO `passenger` (`id`, `active`, `balance`, `blocked`, `acId`, `activationDate`, `validityDate`, `email`, `firstName`, `lastName`, `city_id`) VALUES
	(106, b'1', 300.00, b'0', 20, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail20@yahoo.com', 'John20', 'Doe20', 77),
	(107, b'1', 100.00, b'0', 39, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail39@yahoo.com', 'John39', 'Doe39', 82),
	(108, b'1', 100.00, b'0', 35, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail35@yahoo.com', 'John35', 'Doe35', 75),
	(109, b'1', 100.00, b'0', 14, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail14@yahoo.com', 'John14', 'Doe14', 84),
	(110, b'1', 100.00, b'0', 29, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail29@yahoo.com', 'John29', 'Doe29', 68),
	(111, b'1', 100.00, b'0', 28, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail28@yahoo.com', 'John28', 'Doe28', 78),
	(112, b'1', 100.00, b'0', 2, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail2@yahoo.com', 'John2', 'Doe2', 73),
	(113, b'1', 100.00, b'0', 23, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail23@yahoo.com', 'John23', 'Doe23', 75),
	(114, b'1', 100.00, b'0', 45, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail45@yahoo.com', 'John45', 'Doe45', 72),
	(115, b'1', 100.00, b'0', 40, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail40@yahoo.com', 'John40', 'Doe40', 67),
	(116, b'1', 100.00, b'0', 15, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail15@yahoo.com', 'John15', 'Doe15', 67),
	(117, b'1', 100.00, b'0', 36, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail36@yahoo.com', 'John36', 'Doe36', 69),
	(118, b'1', 100.00, b'0', 33, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail33@yahoo.com', 'John33', 'Doe33', 80),
	(119, b'1', 100.00, b'0', 3, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail3@yahoo.com', 'John3', 'Doe3', 77),
	(120, b'1', 100.00, b'0', 4, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail4@yahoo.com', 'John4', 'Doe4', 78),
	(121, b'1', 100.00, b'0', 41, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail41@yahoo.com', 'John41', 'Doe41', 84),
	(122, b'1', 100.00, b'0', 47, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail47@yahoo.com', 'John47', 'Doe47', 83),
	(123, b'1', 100.00, b'0', 48, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail48@yahoo.com', 'John48', 'Doe48', 76),
	(124, b'1', 100.00, b'0', 25, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail25@yahoo.com', 'John25', 'Doe25', 74),
	(125, b'1', 100.00, b'0', 21, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail21@yahoo.com', 'John21', 'Doe21', 84),
	(126, b'1', 100.00, b'0', 5, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail5@yahoo.com', 'John5', 'Doe5', 68),
	(127, b'1', 100.00, b'0', 46, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail46@yahoo.com', 'John46', 'Doe46', 78),
	(128, b'1', 100.00, b'0', 34, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail34@yahoo.com', 'John34', 'Doe34', 75),
	(129, b'1', 100.00, b'0', 10, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail10@yahoo.com', 'John10', 'Doe10', 80),
	(130, b'1', 100.00, b'0', 11, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail11@yahoo.com', 'John11', 'Doe11', 79),
	(131, b'1', 100.00, b'0', 24, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail24@yahoo.com', 'John24', 'Doe24', 81),
	(132, b'1', 100.00, b'0', 37, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail37@yahoo.com', 'John37', 'Doe37', 68),
	(133, b'1', 100.00, b'0', 44, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail44@yahoo.com', 'John44', 'Doe44', 69),
	(134, b'1', 100.00, b'0', 38, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail38@yahoo.com', 'John38', 'Doe38', 72),
	(135, b'1', 100.00, b'0', 42, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail42@yahoo.com', 'John42', 'Doe42', 80),
	(136, b'1', 100.00, b'0', 43, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail43@yahoo.com', 'John43', 'Doe43', 80),
	(137, b'1', 100.00, b'0', 16, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail16@yahoo.com', 'John16', 'Doe16', 85),
	(138, b'1', 100.00, b'0', 17, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail17@yahoo.com', 'John17', 'Doe17', 70),
	(139, b'1', 100.00, b'0', 6, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail6@yahoo.com', 'John6', 'Doe6', 75),
	(140, b'1', 100.00, b'0', 13, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail13@yahoo.com', 'John13', 'Doe13', 72),
	(141, b'1', 100.00, b'0', 31, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail31@yahoo.com', 'John31', 'Doe31', 78),
	(142, b'1', 100.00, b'0', 27, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail27@yahoo.com', 'John27', 'Doe27', 81),
	(143, b'1', 100.00, b'0', 49, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail49@yahoo.com', 'John49', 'Doe49', 76),
	(144, b'1', 100.00, b'0', 1, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail1@yahoo.com', 'John1', 'Doe1', 69),
	(145, b'1', 100.00, b'0', 9, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail9@yahoo.com', 'John9', 'Doe9', 82),
	(146, b'1', 100.00, b'0', 30, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail30@yahoo.com', 'John30', 'Doe30', 74),
	(147, b'1', 100.00, b'0', 8, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail8@yahoo.com', 'John8', 'Doe8', 83),
	(148, b'1', 100.00, b'0', 32, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail32@yahoo.com', 'John32', 'Doe32', 77),
	(149, b'1', 100.00, b'0', 19, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail19@yahoo.com', 'John19', 'Doe19', 81),
	(150, b'1', 100.00, b'0', 22, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail22@yahoo.com', 'John22', 'Doe22', 78),
	(151, b'1', 100.00, b'0', 0, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail0@yahoo.com', 'John0', 'Doe0', 66),
	(152, b'1', 100.00, b'0', 18, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail18@yahoo.com', 'John18', 'Doe18', 74),
	(153, b'1', 100.00, b'0', 26, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail26@yahoo.com', 'John26', 'Doe26', 75),
	(154, b'1', 100.00, b'0', 12, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail12@yahoo.com', 'John12', 'Doe12', 71),
	(155, b'1', 100.00, b'0', 7, '2019-11-18 23:09:08', '2019-11-18 23:09:08', 'supermail7@yahoo.com', 'John7', 'Doe7', 76);
/*!40000 ALTER TABLE `passenger` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `passenger_action` (
  `id` bigint(20) NOT NULL,
  `costAmont` decimal(19,2) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `journeyleg_id` bigint(20) DEFAULT NULL,
  `point_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnnnfx21e3tmaawuhswkw50l6t` (`journeyleg_id`),
  KEY `FK3syake9t1pyxwbjw167vnb12s` (`point_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `passenger_action` DISABLE KEYS */;
INSERT IGNORE INTO `passenger_action` (`id`, `costAmont`, `date`, `identifier`, `type`, `journeyleg_id`, `point_id`) VALUES
	(256, 0.00, '2019-11-18 23:09:09', '1n4ljQTCMGjt5WwRvIIsvie5rfRKFPb8jRTRXeN7Ep6nlc181eH4BCnbU6cH', 'TOUCH_IN', 206, 20),
	(257, 0.00, '2019-11-18 23:09:09', 'gmqhU3IfCR5875NqqgqFWT8EDtGl00HO6CL3kcjGUYrVxvNBS6SptrWdbUsO', 'TOUCH_IN', 207, 44),
	(258, 0.00, '2019-11-18 23:09:09', '6voq3ONBErhdCXgv0qvqJPwuMVMdyXv11l0FTluUXWVLXRvq2DemqVQO80rV', 'TOUCH_IN', 208, 33),
	(259, 0.00, '2019-11-18 23:09:09', 'vMtXLGoBWWBk7jKDujOQXNkjxMvRkEwIpoLW6e5WdjRB8vo2LbOmtc5gRSCT', 'TOUCH_IN', 209, 27),
	(260, 0.00, '2019-11-18 23:09:09', 'QMwq4E4u2YQMb72xIKFiEEJ2vJR4L0nh2i24MrP7YcwbxpJsCrVm51OLxNdj', 'TOUCH_IN', 210, 54),
	(261, 0.00, '2019-11-18 23:09:09', 'R04BGf0M8BjktJnvM4fKCSqHPnIqqr88Oj17s4SfDfJml0WUMiDtlXSUKF0T', 'TOUCH_IN', 211, 25),
	(262, 0.00, '2019-11-18 23:09:09', 'DxT3RWXiO6rLtwwKYBuYx1plDdJUpTVpDP0qnJd7qtOiTM5cR803odpfNCFk', 'TOUCH_IN', 212, 40),
	(263, 0.00, '2019-11-18 23:09:09', 'FdTdpWSq4VKVJYti1Fwl2kXh41bgqrPX48tEd4Co5jm5SQ04UqBveBMEHflc', 'TOUCH_IN', 213, 1),
	(264, 0.00, '2019-11-18 23:09:09', 'bDD2EmmcRpR1cWXbd3IN68LTqQjCCDVGOHXgFgy8jB642oxvQsUVHJugKM5j', 'TOUCH_IN', 214, 41),
	(265, 0.00, '2019-11-18 23:09:09', 'KQ5SlgX8qVrwhUxwRhBMvxwfFQ5xDsQBK1WFned2DGMXkE6424yYkclbLJTr', 'TOUCH_IN', 215, 50),
	(266, 0.00, '2019-11-18 23:09:09', '1EcbxGOc6gVwtf0BF1jWt4X4ISC6FyiFO6BX0yGf24oi7YSQXmdCbjyR3frt', 'TOUCH_IN', 216, 47),
	(267, 0.00, '2019-11-18 23:09:09', 'R8ocFRmYl5kRFoo21jkoU0s0wqywGnU5C680letxwg0XkSbxiYkblm2dUlp0', 'TOUCH_IN', 217, 36),
	(268, 0.00, '2019-11-18 23:09:09', 'Rm6xLnLDm1NyClp1Nh0lKp3BIjUot8qMWS24S7slQt6xEcYXSY8eUgkpF4NG', 'TOUCH_IN', 218, 18),
	(269, 0.00, '2019-11-18 23:09:09', 'VVWFFUkJGiqnHEm5bxsMgXUnw4UQ0XDunrUS1fRj0odBO4diM2CBGR2GgF6k', 'TOUCH_IN', 219, 24),
	(270, 0.00, '2019-11-18 23:09:09', 'oBHNPUN6Vqfrtf2fcFWw5cjvW5nQIrjiGRruv0RbVsuxoGJTJN244GbD7NRV', 'TOUCH_IN', 220, 57),
	(271, 0.00, '2019-11-18 23:09:09', 'y1MLjQYkXwcn1yvRHBoxuD4kqWohHNkg1vlYjMrgODJoKt3HrjnswbIpHYh7', 'TOUCH_IN', 221, 42),
	(272, 0.00, '2019-11-18 23:09:09', 'VscgsfYoqbpmKB5U0iLE6I0bgQb2HQM21rDYV1tcDfYJLSbHcv27gnePFqmS', 'TOUCH_IN', 222, 16),
	(273, 0.00, '2019-11-18 23:09:09', 'LuUiuDFcb0jbsmxEeEgtidCmNo0Kbf54eCGBUyh1pkvK5wMsMV55FfB3vwsN', 'TOUCH_IN', 223, 31),
	(274, 0.00, '2019-11-18 23:09:09', 'HFBoI38TVTWEToXbchPrsfuYgqj48oxiTIrd7VrHmH61Fb8iT8nft5Q840XT', 'TOUCH_IN', 224, 29),
	(275, 0.00, '2019-11-18 23:09:09', 'p0NEXn1d6K0xkQdXij41Ui6NwNvqTOwlUjWqtHd5ldN3ntVLbt0yC7kLq2Pg', 'TOUCH_IN', 225, 38),
	(276, 0.00, '2019-11-18 23:09:09', 'VnCSCMUdXNfgj163qCmnS7g7rj6q2MVuy483lmySBpVFuI5SsgRyO6NdPtqo', 'TOUCH_IN', 226, 57),
	(277, 0.00, '2019-11-18 23:09:09', 'BwiBIsbkTWiodF3yJf7I1k2jlj7U0O6UYhpvxpo3K3RQjy3n4SX4OkHJ0QpX', 'TOUCH_IN', 227, 54),
	(278, 0.00, '2019-11-18 23:09:09', 'MmsLMvqyYXqq3PyImk6v26y061pghkq8XtM4DW31L0MeJOpOB4VWDFNLLNn8', 'TOUCH_IN', 228, 24),
	(279, 0.00, '2019-11-18 23:09:09', 'RN7iIwnoxCSDPigo0ktNJn6bWNu12fuJDJyOvJViEfDyMKSVj7OePppnRvcp', 'TOUCH_IN', 229, 57),
	(280, 0.00, '2019-11-18 23:09:09', 'nlgoxveQIBED6tH4tqTrBlQgwVoFQmI25DR3uHeybSC6gIT2Gy8nid0wuUeb', 'TOUCH_IN', 230, 44),
	(281, 0.00, '2019-11-18 23:09:09', '3sf2I1Di5BSoT6MLmdtLK8jiMQPRPxX3hFdnk401THBXGl4dVc5unUhlWRLQ', 'TOUCH_IN', 231, 54),
	(282, 0.00, '2019-11-18 23:09:09', 'LCBfEIVJG2h7GCniXis6MuW2cgbUu5EmyEseucXn5WPoYTDXvWpHwHWYngc3', 'TOUCH_IN', 232, 20),
	(283, 0.00, '2019-11-18 23:09:09', 'r2tsRoFNDTSnmSgtkL5ymXigl4cJGeJr5vVFLBEmMhohSdX3I743F6QKUDvd', 'TOUCH_IN', 233, 33),
	(284, 0.00, '2019-11-18 23:09:09', 'wo5QNWD0Elv1myYr6gHojDfumuLqQ5i6s4Y57CU0PDg1In8wQNb0S3sTjlui', 'TOUCH_IN', 234, 18),
	(285, 0.00, '2019-11-18 23:09:09', 'rWCRNb2CutNVjYDKi5QFt8hDMV3D3DpgQmlRiQ83mnVIDgGjfrfLj6WNcYWf', 'TOUCH_IN', 235, 1),
	(286, 0.00, '2019-11-18 23:09:09', 'GCWMPxJtgptukqY3MrW0qX7LM6q2sHf4dd6JGLLH2kOUwmEC1oxTf36hCrgo', 'TOUCH_IN', 236, 27),
	(287, 0.00, '2019-11-18 23:09:09', 'PDoDiKsI5OjQWKxvwiqlhOyqVqddNtp0upcrbq7gFd7oYt6Yw3sWOMclxXRi', 'TOUCH_IN', 237, 25),
	(288, 0.00, '2019-11-18 23:09:09', 'Fe8sG6BPUUhgmUGRRPNjKUGHYtox8H8pM6fwR4kinkuJuKGO3bJs6LyUFiKR', 'TOUCH_IN', 238, 57),
	(289, 0.00, '2019-11-18 23:09:09', 'Pjp772fxh1syov3KM5Hg6TOKNSB4wokHd5pTWCqoyGTrWJE5iKogEGm0VyE2', 'TOUCH_IN', 239, 57),
	(290, 0.00, '2019-11-18 23:09:09', 'NDGVb14sglBykdveU8rqihgHi00yY82MonKqwVyH5jX72ht5fkMGNmXvW2GX', 'TOUCH_IN', 240, 18),
	(291, 0.00, '2019-11-18 23:09:09', '0d6WJSDiEcbESLkutoMKtPKbnduUO5xlXJoE1NnkCxxueCLQrnwmbecdcjnT', 'TOUCH_IN', 241, 41),
	(292, 0.00, '2019-11-18 23:09:09', '210v6FuLbvvdnrJ4j66occc3JwjhVxSyi02qHiwPIHIHXecgN2O4im3U4PYy', 'TOUCH_IN', 242, 40),
	(293, 0.00, '2019-11-18 23:09:09', 'eey3HQMChNGsuogW5qXeGU8H1t2Dxk4240iwKFmd6DmfT1CiE4XmUU5jpXsV', 'TOUCH_IN', 243, 40),
	(294, 0.00, '2019-11-18 23:09:09', 'kUjBwTLT0KlFEpgiMljPnflrFDr6N8Uqv0yC1dFODlewVHdhdb7NWYtlFxDB', 'TOUCH_IN', 244, 29),
	(295, 0.00, '2019-11-18 23:09:09', 'cyRyoIBnyJqom7TGR150YMQTrpWNxhORVCkqpeoK7djUlN6CVgCwUNvBFWxc', 'TOUCH_IN', 245, 18),
	(296, 0.00, '2019-11-18 23:09:09', 'K481p2rqBkyXVn8sVx2N6LkkNu60RlIv5o3gBJmqc2Klu0oRosgf5dURVXGQ', 'TOUCH_IN', 246, 18),
	(297, 0.00, '2019-11-18 23:09:09', 'y3dq613yMSTmttBpYdQUlI8qmIXTKHVeErqd3PvRXEosuKohLd7q81Y7PJOI', 'TOUCH_IN', 247, 57),
	(298, 0.00, '2019-11-18 23:09:09', 'oqxy3JUHVeTBtHdP1xdTi0lhNoX8nl8xJQI6hJJnNqcmUEd20TRu3JGcfFGK', 'TOUCH_IN', 248, 16),
	(299, 0.00, '2019-11-18 23:09:09', 'u5vh7E2UgpyrtypxttLEr15ekk1XjlbwoFnGBuVw6tePw4SEu6v6GxDPH1h2', 'TOUCH_IN', 249, 20),
	(300, 0.00, '2019-11-18 23:09:09', 'H4SI3WowFpnobJTEkqdppkJX2S207UcVfTyH2ksOSUxeF0H0mddMpyTUXUD2', 'TOUCH_IN', 250, 24),
	(301, 0.00, '2019-11-18 23:09:09', 'brBdsWGgvQ8KOOFiq1TqNbb7ppoybCLEl8yVGUmk8yerSFWInfiehTjBIOM8', 'TOUCH_IN', 251, 38),
	(302, 0.00, '2019-11-18 23:09:09', 'y3ucptbfIYkxjgqpnI0n00Py71djNUGmcPVVjnlFJshfoQSTw7vmIcMVCrfd', 'TOUCH_IN', 252, 27),
	(303, 0.00, '2019-11-18 23:09:09', 'RSrhNmJt53n0KrJvQg2GN1Xdh3x2TWlvtUDRHd5cc6ITgBTUVeVI87t0l53e', 'TOUCH_IN', 253, 25),
	(304, 0.00, '2019-11-18 23:09:09', 'k3jNnC3tFvJeTyD6RQEssDnt4d1oWLt2QDJEYJmvvFlwQJsnJkkdJeojJb0W', 'TOUCH_IN', 254, 20),
	(305, 0.00, '2019-11-18 23:09:09', 'bN6QVOBHYk30YNC1l2boXr5Jhp02WxujHu3WUDPmIccXV1tkKsUGijYdQILq', 'TOUCH_IN', 255, 44);
/*!40000 ALTER TABLE `passenger_action` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `point` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `point` DISABLE KEYS */;
INSERT IGNORE INTO `point` (`id`, `name`) VALUES
	(1, 'point 16'),
	(16, 'point 15'),
	(18, 'point 14'),
	(20, 'point 13'),
	(24, 'point 9'),
	(25, 'point 12'),
	(27, 'point 8'),
	(29, 'point 11'),
	(31, 'point 7'),
	(33, 'point 10'),
	(36, 'point 6'),
	(38, 'point 5'),
	(40, 'point 4'),
	(41, 'point 3'),
	(42, 'point 2'),
	(44, 'point 1'),
	(47, 'point 0'),
	(50, 'point 19'),
	(54, 'point 18'),
	(57, 'point 17');
/*!40000 ALTER TABLE `point` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `vehicle` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT IGNORE INTO `vehicle` (`id`, `name`, `slug`, `type`) VALUES
	(3, 'Underground', 'underground', 'UNDERGROUND'),
	(6, 'Train', 'train', 'TRAIN'),
	(9, 'Cablecar', 'cablecar', 'CABLECAR'),
	(12, 'Overground', 'overground', 'OVERGROUND'),
	(22, 'Dlr', 'dlr', 'DLR');
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
