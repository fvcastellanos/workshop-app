-- -----------------------------------------------------
-- Table `workshop`.`sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workshop`.`sequence` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `prefix` VARCHAR(5) NOT NULL,
  `value` VARCHAR(30) NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_sequence_prefix` (`prefix` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;
