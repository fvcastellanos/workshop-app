ALTER TABLE workshop.inventory ADD operation_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
CREATE INDEX idx_inventory_operation_date USING BTREE ON workshop.inventory (operation_date);
