CREATE TABLE IF NOT EXISTS `product_category` (
  `id` VARCHAR(50) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(300) NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` INT NOT NULL DEFAULT '1',
  `tenant` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

ALTER TABLE workshop.product ADD product_category_id varchar(50) NULL;
ALTER TABLE workshop.product CHANGE product_category_id product_category_id varchar(50) NULL AFTER id;
ALTER TABLE workshop.product ADD CONSTRAINT product_product_category_id_FK FOREIGN KEY (product_category_id) REFERENCES workshop.product_category(id);
