use workshop;

ALTER TABLE workshop.work_order ADD order_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
CREATE INDEX work_order_order_date_IDX USING BTREE ON workshop.work_order (order_date);
