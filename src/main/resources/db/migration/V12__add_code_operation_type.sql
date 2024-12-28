ALTER TABLE workshop.operation_type ADD code varchar(50) NOT NULL;
CREATE INDEX operation_type_code_IDX USING BTREE ON workshop.operation_type (code);
