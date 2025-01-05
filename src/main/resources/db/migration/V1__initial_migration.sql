-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema workshop
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema workshop
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `workshop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin ;
USE `workshop` ;

-- -----------------------------------------------------
-- Table `workshop`.`car_brand`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`car_brand` (
  `id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_car_brand_name` (`name` ASC, `tenant` ASC) VISIBLE,
  INDEX `idx_car_brand_created` (`created` ASC) VISIBLE,
  INDEX `idx_car_brand_active` (`active` ASC) INVISIBLE,
  INDEX `idx_car_brand_tenant` (`tenant` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`car_line`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`car_line` (
  `id` VARCHAR(50) NOT NULL,
  `car_brand_id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `active` INT UNSIGNED NOT NULL DEFAULT '1',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_car_line_name` (`name` ASC, `tenant` ASC) INVISIBLE,
  INDEX `idx_car_line_created` (`created` ASC) INVISIBLE,
  INDEX `idx_car_line_active` (`active` ASC) INVISIBLE,
  INDEX `idx_car_line_tenant` (`tenant` ASC) VISIBLE,
  INDEX `fk_car_line_car_brand1_idx` (`car_brand_id` ASC) VISIBLE,
  CONSTRAINT `fk_car_line_car_brand1`
    FOREIGN KEY (`car_brand_id`)
    REFERENCES `workshop`.`car_brand` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`contact` (
  `id` VARCHAR(50) NOT NULL,
  `type` CHAR(1) NOT NULL DEFAULT 'C',
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `contact` VARCHAR(150) NULL DEFAULT NULL,
  `tax_id` VARCHAR(50) NULL DEFAULT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_contact_code` (`code` ASC, `tenant` ASC) INVISIBLE,
  INDEX `idx_contact_created` (`created` ASC) VISIBLE,
  INDEX `idx_contact_tax_id` (`tax_id` ASC) VISIBLE,
  INDEX `idx_contact_updated` (`updated` ASC) VISIBLE,
  INDEX `idx_contact_tenant` (`tenant` ASC) VISIBLE,
  INDEX `idx_contact_active` (`active` ASC) VISIBLE,
  INDEX `idx_contat_type` (`type` ASC) VISIBLE,
  INDEX `idx_contact_name` USING BTREE (`name`) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`discount_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`discount_type` (
  `id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_discount_type_created` (`created` ASC) VISIBLE,
  INDEX `idx_discount_type_updated` (`updated` ASC) VISIBLE,
  INDEX `idx_discount_type_tenantId` (`tenant` ASC) VISIBLE,
  INDEX `idx_discount_type_active` (`active` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`flyway_schema_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`flyway_schema_history` (
  `installed_rank` INT NOT NULL,
  `version` VARCHAR(50) NULL DEFAULT NULL,
  `description` VARCHAR(200) NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `script` VARCHAR(1000) NOT NULL,
  `checksum` INT NULL DEFAULT NULL,
  `installed_by` VARCHAR(100) NOT NULL,
  `installed_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` INT NOT NULL,
  `success` TINYINT(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  INDEX `flyway_schema_history_s_idx` (`success` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`operation_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`operation_type` (
  `id` VARCHAR(50) NOT NULL,
  `type` CHAR(1) NOT NULL DEFAULT 'I',
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_operation_type_created` (`created` ASC) VISIBLE,
  INDEX `idx_operation_type_updated` (`updated` ASC) VISIBLE,
  INDEX `idx_operation_type_tenant_id` (`tenant` ASC) INVISIBLE,
  INDEX `idx_operation_type_active` (`active` ASC) VISIBLE,
  INDEX `idx_operation_type_type` (`type` ASC) VISIBLE,
  INDEX `operation_type_code_IDX` USING BTREE (`code`) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`product_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`product_category` (
  `id` VARCHAR(50) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`product` (
  `id` VARCHAR(50) NOT NULL,
  `product_category_id` VARCHAR(50) NULL DEFAULT NULL,
  `type` CHAR(1) NOT NULL DEFAULT 'P',
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `minimal_quantity` DOUBLE NOT NULL DEFAULT '0',
  `sale_price` DOUBLE NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_product_code` (`code` ASC, `tenant` ASC) VISIBLE,
  INDEX `idx_product_created` (`created` ASC) VISIBLE,
  INDEX `idx_product_updated` (`updated` ASC) VISIBLE,
  INDEX `idx_product_tenant` (`tenant` ASC) VISIBLE,
  INDEX `idx_product_active` (`active` ASC) VISIBLE,
  INDEX `idx_product_type` (`type` ASC) VISIBLE,
  INDEX `product_product_category_id_FK` (`product_category_id` ASC) VISIBLE,
  CONSTRAINT `product_product_category_id_FK`
    FOREIGN KEY (`product_category_id`)
    REFERENCES `workshop`.`product_category` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`inventory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`inventory` (
  `id` VARCHAR(50) NOT NULL,
  `product_id` VARCHAR(50) NOT NULL,
  `invoice_detail_id` VARCHAR(50) NULL DEFAULT NULL,
  `quantity` DOUBLE NOT NULL DEFAULT '1',
  `unit_price` DOUBLE NOT NULL DEFAULT '0',
  `discount_amount` DOUBLE NOT NULL DEFAULT '0',
  `total` DOUBLE NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` VARCHAR(200) NULL DEFAULT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tenant` VARCHAR(50) NOT NULL,
  `operation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `operation_type_id` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_inventory_movement_tenant` (`product_id` ASC, `invoice_detail_id` ASC, `tenant` ASC, `operation_type_id` ASC) VISIBLE,
  INDEX `idx_inventory_created` (`created` ASC) VISIBLE,
  INDEX `idx_inventory_updated` (`updated` ASC) VISIBLE,
  INDEX `idx_inventory_tenant_id` (`tenant` ASC) VISIBLE,
  INDEX `fk_inventory_product1_idx` (`product_id` ASC) VISIBLE,
  INDEX `idx_inventory_operation_date` USING BTREE (`operation_date`) VISIBLE,
  INDEX `fk_inventory_operation_type` (`operation_type_id` ASC) VISIBLE,
  CONSTRAINT `fk_inventory_operation_type`
    FOREIGN KEY (`operation_type_id`)
    REFERENCES `workshop`.`operation_type` (`id`),
  CONSTRAINT `fk_inventory_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `workshop`.`product` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`invoice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`invoice` (
  `id` VARCHAR(50) NOT NULL,
  `contact_id` VARCHAR(50) NOT NULL,
  `type` VARCHAR(45) NOT NULL DEFAULT 'P',
  `suffix` VARCHAR(30) NULL DEFAULT NULL,
  `number` VARCHAR(100) NOT NULL,
  `image_url` VARCHAR(250) NULL DEFAULT NULL,
  `invoice_date` TIMESTAMP NOT NULL,
  `effective_date` TIMESTAMP NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tenant` VARCHAR(50) NOT NULL,
  `status` CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_invoice_number` (`suffix` ASC, `number` ASC, `tenant` ASC, `type` ASC, `contact_id` ASC) VISIBLE,
  INDEX `idx_invoice_created` (`created` ASC) VISIBLE,
  INDEX `idx_invoice_updated` (`updated` ASC) VISIBLE,
  INDEX `idx_invoice_tenant` (`tenant` ASC) VISIBLE,
  INDEX `fk_provider_invoice_contact1_idx` (`contact_id` ASC) VISIBLE,
  INDEX `idx_invoice_type` (`type` ASC) VISIBLE,
  INDEX `idx_invoice_date` (`invoice_date` ASC) INVISIBLE,
  INDEX `idx_invoice_effective_date` (`effective_date` ASC) INVISIBLE,
  INDEX `idx_invoice_status` (`status` ASC) VISIBLE,
  CONSTRAINT `fk_provider_invoice_contact1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `workshop`.`contact` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`work_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`work_order` (
  `id` VARCHAR(50) NOT NULL,
  `car_line_id` VARCHAR(50) NOT NULL,
  `contact_id` VARCHAR(50) NOT NULL,
  `number` VARCHAR(100) NOT NULL,
  `status` CHAR(1) NOT NULL DEFAULT 'P',
  `plate_number` VARCHAR(30) NOT NULL,
  `odometer_measurement` CHAR(1) NOT NULL DEFAULT 'K',
  `odometer_value` DOUBLE NOT NULL,
  `gas_amount` DOUBLE NOT NULL DEFAULT '0',
  `tenant` VARCHAR(50) NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` TEXT NULL DEFAULT NULL,
  `order_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_work_order_number` (`number` ASC, `tenant` ASC) VISIBLE,
  INDEX `fk_work_order_car_line1_idx` (`car_line_id` ASC) VISIBLE,
  INDEX `fk_work_order_contact1_idx` (`contact_id` ASC) VISIBLE,
  INDEX `idx_work_order_plate_number` (`plate_number` ASC) VISIBLE,
  INDEX `work_order_order_date_IDX` USING BTREE (`order_date`) VISIBLE,
  CONSTRAINT `fk_work_order_car_line1`
    FOREIGN KEY (`car_line_id`)
    REFERENCES `workshop`.`car_line` (`id`),
  CONSTRAINT `fk_work_order_contact1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `workshop`.`contact` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`invoice_detail`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`invoice_detail` (
  `id` VARCHAR(50) NOT NULL,
  `invoice_id` VARCHAR(50) NOT NULL,
  `product_id` VARCHAR(50) NOT NULL,
  `work_order_id` VARCHAR(50) NULL DEFAULT NULL,
  `quantity` DOUBLE NOT NULL DEFAULT '1',
  `unit_price` DOUBLE NOT NULL DEFAULT '0',
  `discount_amount` DOUBLE NOT NULL DEFAULT '0',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_invoice_detail_detail` (`invoice_id` ASC, `product_id` ASC, `tenant` ASC) VISIBLE,
  INDEX `fk_invoice_detail_invoice1_idx` (`invoice_id` ASC) VISIBLE,
  INDEX `fk_invoice_detail_product1_idx` (`product_id` ASC) VISIBLE,
  INDEX `fk_invoice_detail_work_order1_idx` (`work_order_id` ASC) VISIBLE,
  CONSTRAINT `fk_invoice_detail_invoice1`
    FOREIGN KEY (`invoice_id`)
    REFERENCES `workshop`.`invoice` (`id`),
  CONSTRAINT `fk_invoice_detail_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `workshop`.`product` (`id`),
  CONSTRAINT `fk_invoice_detail_work_order1`
    FOREIGN KEY (`work_order_id`)
    REFERENCES `workshop`.`work_order` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`sequence` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `prefix` VARCHAR(5) NOT NULL,
  `value` VARCHAR(30) NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` VARCHAR(300) NULL DEFAULT NULL,
  `tenant` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_sequence_prefix_tenant` (`prefix` ASC, `tenant` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`tenant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`tenant` (
  `id` VARCHAR(50) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `active` INT NOT NULL DEFAULT '1',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_tenant_code` (`code` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`user` (
  `id` VARCHAR(50) NOT NULL,
  `tenant_id` VARCHAR(50) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(150) NOT NULL,
  `active` INT NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_user_user_id` (`user_id` ASC, `provider` ASC) VISIBLE,
  INDEX `fk_user_tenant1_idx` (`tenant_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_tenant1`
    FOREIGN KEY (`tenant_id`)
    REFERENCES `workshop`.`tenant` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `workshop`.`work_order_detail`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`work_order_detail` (
  `id` VARCHAR(50) NOT NULL,
  `work_order_id` VARCHAR(50) NOT NULL,
  `product_id` VARCHAR(50) NOT NULL,
  `invoice_detail_id` VARCHAR(50) NULL DEFAULT NULL,
  `quantity` DOUBLE NOT NULL DEFAULT '0',
  `unit_price` DOUBLE NOT NULL DEFAULT '0',
  `tenant` VARCHAR(50) NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_work_order_detail_details` (`work_order_id` ASC, `product_id` ASC, `tenant` ASC) VISIBLE,
  INDEX `fk_work_order_detail_work_order1_idx` (`work_order_id` ASC) VISIBLE,
  INDEX `fk_work_order_detail_product1_idx` (`product_id` ASC) VISIBLE,
  INDEX `idx_work_order_detail_tenant` (`tenant` ASC) VISIBLE,
  INDEX `fk_work_order_detail_invoice_detail1_idx` (`invoice_detail_id` ASC) VISIBLE,
  CONSTRAINT `fk_work_order_detail_invoice_detail1`
    FOREIGN KEY (`invoice_detail_id`)
    REFERENCES `workshop`.`invoice_detail` (`id`),
  CONSTRAINT `fk_work_order_detail_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `workshop`.`product` (`id`),
  CONSTRAINT `fk_work_order_detail_work_order1`
    FOREIGN KEY (`work_order_id`)
    REFERENCES `workshop`.`work_order` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

USE `workshop` ;

-- -----------------------------------------------------
-- function generate_ksuid
-- -----------------------------------------------------

DELIMITER $$
USE `workshop`$$
CREATE DEFINER=`root`@`%` FUNCTION `generate_ksuid`() RETURNS char(27) CHARSET utf8mb4 COLLATE utf8mb4_bin
    DETERMINISTIC
BEGIN
    DECLARE digits CHAR(62) DEFAULT "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    DECLARE n DECIMAL(49) DEFAULT UNIX_TIMESTAMP() - 1400000000; -- 8 byte time portion offset per specification.
    DECLARE f DECIMAL(49) DEFAULT 4294967296;
    DECLARE s CHAR(27) DEFAULT "";
    DECLARE i INT DEFAULT 1;

    -- Add in 32 bytes of random data
    SET n = n * f + CAST(RAND() * f AS UNSIGNED);
    SET n = n * f + CAST(RAND() * f AS UNSIGNED);
    SET n = n * f + CAST(RAND() * f AS UNSIGNED);
    SET n = n * f + CAST(RAND() * f AS UNSIGNED);

    -- Base62 encoding
    WHILE i <= 27 DO
        SET s = CONCAT(SUBSTR(digits, (n MOD 62) + 1, 1), s);
        SET n = FLOOR(n / 62);
        SET i = i + 1;
    END WHILE;

    RETURN s;

END$$

DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
