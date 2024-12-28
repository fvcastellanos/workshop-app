ALTER TABLE workshop.inventory ADD operation_type_id varchar(50) NOT NULL;

ALTER TABLE `workshop`.`inventory`
  ADD CONSTRAINT `fk_inventory_operation_type`
    FOREIGN KEY (`operation_type_id`)
    REFERENCES `workshop`.`operation_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
;

ALTER TABLE `workshop`.`inventory`
    DROP COLUMN `operation_type`,
    DROP INDEX `uq_inventory_movement_tenant` ,
    ADD UNIQUE INDEX `uq_inventory_movement_tenant` (`product_id` ASC, `invoice_detail_id` ASC, `tenant` ASC, `operation_type_id` ASC) VISIBLE,
    DROP INDEX `idx_inventory_operation_type` ;
;
