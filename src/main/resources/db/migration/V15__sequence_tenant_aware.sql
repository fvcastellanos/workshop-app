ALTER TABLE workshop.`sequence` ADD description varchar(300) NULL;
ALTER TABLE workshop.`sequence` ADD tenant varchar(50) NULL;

ALTER TABLE `workshop`.`sequence` DROP INDEX `uq_sequence_prefix`;
ALTER TABLE `workshop`.`sequence` ADD UNIQUE INDEX `uq_sequence_prefix_tenant` (`prefix`, `tenant`);

update workshop.`sequence` set tenant = 'resta';
