-- -----------------------------------------------------
-- Schema workshop
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS workshop;

-- -----------------------------------------------------
-- Table workshop.car_brand
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.car_brand (
  id VARCHAR(50) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(300) DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active INT NOT NULL DEFAULT 1,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name, tenant)
);

-- Indexes for workshop.car_brand
CREATE INDEX idx_car_brand_created ON workshop.car_brand (created);
CREATE INDEX idx_car_brand_active ON workshop.car_brand (active);
CREATE INDEX idx_car_brand_tenant ON workshop.car_brand (tenant);


-- -----------------------------------------------------
-- Table workshop.car_line
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.car_line (
  id VARCHAR(50) NOT NULL,
  car_brand_id VARCHAR(50) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(300) DEFAULT NULL,
  active INT NOT NULL DEFAULT 1,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name, tenant),
  CONSTRAINT fk_car_line_car_brand1 FOREIGN KEY (car_brand_id)
    REFERENCES workshop.car_brand (id)
);

-- Indexes for workshop.car_line
CREATE INDEX idx_car_line_created ON workshop.car_line (created);
CREATE INDEX idx_car_line_active ON workshop.car_line (active);
CREATE INDEX idx_car_line_tenant ON workshop.car_line (tenant);


-- -----------------------------------------------------
-- Table workshop.contact
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.contact (
  id VARCHAR(50) NOT NULL,
  type CHAR(1) NOT NULL DEFAULT 'C',
  code VARCHAR(50) NOT NULL,
  name VARCHAR(150) NOT NULL,
  contact VARCHAR(150) DEFAULT NULL,
  tax_id VARCHAR(50) DEFAULT NULL,
  description VARCHAR(300) DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active INT NOT NULL DEFAULT 1,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (code, tenant)
);

-- Indexes for workshop.contact
CREATE INDEX idx_contact_created ON workshop.contact (created);
CREATE INDEX idx_contact_tax_id ON workshop.contact (tax_id);
CREATE INDEX idx_contact_updated ON workshop.contact (updated);
CREATE INDEX idx_contact_tenant ON workshop.contact (tenant);
CREATE INDEX idx_contact_active ON workshop.contact (active);
CREATE INDEX idx_contat_type ON workshop.contact (type);
CREATE INDEX idx_contact_name ON workshop.contact (name);

-- -----------------------------------------------------
-- Table workshop.discount_type
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.discount_type (
  id VARCHAR(50) NOT NULL,
  name VARCHAR(150) NOT NULL,
  description VARCHAR(300) DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active INT NOT NULL DEFAULT 1,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

create index if not exists idx_discount_type_created on workshop.discount_type (created);
create index if not exists idx_discount_type_updated on workshop.discount_type (updated);
create index if not exists idx_discount_type_tenantId on workshop.discount_type (tenant);
create index if not exists idx_discount_type_active on workshop.discount_type (active);

-- -----------------------------------------------------
-- Table workshop.operation_type
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.operation_type (
  id VARCHAR(50) NOT NULL,
  type CHAR(1) NOT NULL DEFAULT 'I',
  name VARCHAR(150) NOT NULL,
  description VARCHAR(300) DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active INT NOT NULL DEFAULT 1,
  tenant VARCHAR(50) NOT NULL,
  code VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

-- Indexes for workshop.operation_type
CREATE INDEX idx_operation_type_created ON workshop.operation_type (created);
CREATE INDEX idx_operation_type_updated ON workshop.operation_type (updated);
CREATE INDEX idx_operation_type_tenant_id ON workshop.operation_type (tenant);
CREATE INDEX idx_operation_type_active ON workshop.operation_type (active);
CREATE INDEX idx_operation_type_type ON workshop.operation_type (type);
CREATE INDEX operation_type_code_IDX ON workshop.operation_type (code);

-- -----------------------------------------------------
-- Table workshop.product_category
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.product_category (
  id VARCHAR(50) NOT NULL,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(150) NOT NULL,
  description VARCHAR(300) DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active INT NOT NULL DEFAULT 1,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

create index if not exists idx_product_category_code on product_category (code);
create index if not exists idx_product_category_tenant on product_category (tenant);
create index if not exists idx_product_category_active on product_category (active);

-- -----------------------------------------------------
-- Table workshop.product
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.product (
  id VARCHAR(50) NOT NULL,
  product_category_id VARCHAR(50) DEFAULT NULL,
  type CHAR(1) NOT NULL DEFAULT 'P',
  code VARCHAR(50) NOT NULL,
  name VARCHAR(150) NOT NULL,
  description VARCHAR(300) DEFAULT NULL,
  minimal_quantity DOUBLE PRECISION NOT NULL DEFAULT 0,
  sale_price DOUBLE PRECISION NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active INT NOT NULL DEFAULT 1,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (code, tenant),
  CONSTRAINT product_product_category_id_FK
    FOREIGN KEY (product_category_id)
    REFERENCES workshop.product_category (id)
);

-- Indexes for workshop.product
CREATE INDEX idx_product_created ON workshop.product (created);
CREATE INDEX idx_product_updated ON workshop.product (updated);
CREATE INDEX idx_product_tenant ON workshop.product (tenant);
CREATE INDEX idx_product_active ON workshop.product (active);
CREATE INDEX idx_product_type ON workshop.product (type);


-- -----------------------------------------------------
-- Table workshop.inventory
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.inventory (
  id VARCHAR(50) NOT NULL,
  product_id VARCHAR(50) NOT NULL,
  invoice_detail_id VARCHAR(50) DEFAULT NULL,
  quantity DOUBLE PRECISION NOT NULL DEFAULT 1,
  unit_price DOUBLE PRECISION NOT NULL DEFAULT 0,
  discount_amount DOUBLE PRECISION NOT NULL DEFAULT 0,
  total DOUBLE PRECISION NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description VARCHAR(200) DEFAULT NULL,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tenant VARCHAR(50) NOT NULL,
  operation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  operation_type_id VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (product_id, invoice_detail_id, tenant, operation_type_id),
  CONSTRAINT fk_inventory_operation_type
    FOREIGN KEY (operation_type_id)
    REFERENCES workshop.operation_type (id),
  CONSTRAINT fk_inventory_product
    FOREIGN KEY (product_id)
    REFERENCES workshop.product (id)
);

-- Indexes for workshop.inventory
CREATE INDEX idx_inventory_created ON workshop.inventory (created);
CREATE INDEX idx_inventory_updated ON workshop.inventory (updated);
CREATE INDEX idx_inventory_tenant_id ON workshop.inventory (tenant);
CREATE INDEX idx_inventory_product_id ON workshop.inventory (product_id);
CREATE INDEX idx_inventory_operation_date ON workshop.inventory (operation_date);
CREATE INDEX idx_inventory_operation_type_id ON workshop.inventory (operation_type_id);


-- -----------------------------------------------------
-- Table workshop.invoice
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.invoice (
  id VARCHAR(50) NOT NULL,
  contact_id VARCHAR(50) NOT NULL,
  type VARCHAR(45) NOT NULL DEFAULT 'P',
  suffix VARCHAR(30) DEFAULT NULL,
  number VARCHAR(100) NOT NULL,
  image_url VARCHAR(250) DEFAULT NULL,
  invoice_date TIMESTAMP NOT NULL,
  effective_date TIMESTAMP DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tenant VARCHAR(50) NOT NULL,
  status CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (id),
  UNIQUE (suffix, number, tenant, type, contact_id),
  CONSTRAINT fk_invoice_contact
    FOREIGN KEY (contact_id)
    REFERENCES workshop.contact (id)
);

-- Indexes for workshop.invoice
CREATE INDEX idx_invoice_created ON workshop.invoice (created);
CREATE INDEX idx_invoice_updated ON workshop.invoice (updated);
CREATE INDEX idx_invoice_tenant ON workshop.invoice (tenant);
CREATE INDEX idx_invoice_contact_id ON workshop.invoice (contact_id);
CREATE INDEX idx_invoice_type ON workshop.invoice (type);
CREATE INDEX idx_invoice_date ON workshop.invoice (invoice_date);
CREATE INDEX idx_invoice_effective_date ON workshop.invoice (effective_date);
CREATE INDEX idx_invoice_status ON workshop.invoice (status);


-- -----------------------------------------------------
-- Table workshop.work_order
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.work_order (
  id VARCHAR(50) NOT NULL,
  car_line_id VARCHAR(50) NOT NULL,
  contact_id VARCHAR(50) NOT NULL,
  number VARCHAR(100) NOT NULL,
  status CHAR(1) NOT NULL DEFAULT 'P',
  plate_number VARCHAR(30) NOT NULL,
  odometer_measurement CHAR(1) NOT NULL DEFAULT 'K',
  odometer_value DOUBLE PRECISION NOT NULL,
  gas_amount DOUBLE PRECISION NOT NULL DEFAULT 0,
  tenant VARCHAR(50) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  notes TEXT DEFAULT NULL,
  order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (number, tenant),
  CONSTRAINT fk_work_order_car_line
    FOREIGN KEY (car_line_id)
    REFERENCES workshop.car_line (id),
  CONSTRAINT fk_work_order_contact
    FOREIGN KEY (contact_id)
    REFERENCES workshop.contact (id)
);

-- Indexes for workshop.work_order
CREATE INDEX idx_work_order_car_line_id ON workshop.work_order (car_line_id);
CREATE INDEX idx_work_order_contact_id ON workshop.work_order (contact_id);
CREATE INDEX idx_work_order_plate_number ON workshop.work_order (plate_number);
CREATE INDEX idx_work_order_order_date ON workshop.work_order (order_date);

-- -----------------------------------------------------
-- Table workshop.invoice_detail
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.invoice_detail (
  id VARCHAR(50) NOT NULL,
  invoice_id VARCHAR(50) NOT NULL,
  product_id VARCHAR(50) NOT NULL,
  work_order_id VARCHAR(50) DEFAULT NULL,
  quantity DOUBLE PRECISION NOT NULL DEFAULT 1,
  unit_price DOUBLE PRECISION NOT NULL DEFAULT 0,
  discount_amount DOUBLE PRECISION NOT NULL DEFAULT 0,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tenant VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (invoice_id, product_id, tenant),
  CONSTRAINT fk_invoice_detail_invoice
    FOREIGN KEY (invoice_id)
    REFERENCES workshop.invoice (id),
  CONSTRAINT fk_invoice_detail_product
    FOREIGN KEY (product_id)
    REFERENCES workshop.product (id),
  CONSTRAINT fk_invoice_detail_work_order
    FOREIGN KEY (work_order_id)
    REFERENCES workshop.work_order (id)
);

-- Indexes for workshop.invoice_detail
CREATE INDEX idx_invoice_detail_invoice_id ON workshop.invoice_detail (invoice_id);
CREATE INDEX idx_invoice_detail_product_id ON workshop.invoice_detail (product_id);
CREATE INDEX idx_invoice_detail_work_order_id ON workshop.invoice_detail (work_order_id);

-- -----------------------------------------------------
-- Table workshop.sequence
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.sequence (
  id SERIAL PRIMARY KEY,
  prefix VARCHAR(5) NOT NULL,
  value VARCHAR(30) NOT NULL,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description VARCHAR(300) DEFAULT NULL,
  tenant VARCHAR(50) DEFAULT NULL,
  UNIQUE (prefix, tenant)
);

create index idx_sequence_tenant on workshop.sequence (tenant);

-- -----------------------------------------------------
-- Table workshop.tenant
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.tenant (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(150) NOT NULL,
  active INT NOT NULL DEFAULT 1,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for workshop.tenant
CREATE INDEX idx_tenant_code ON workshop.tenant (code);

-- -----------------------------------------------------
-- Table workshop.application_user
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.application_user (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  tenant_id VARCHAR(50) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  user_id VARCHAR(150) NOT NULL,
  active INT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  UNIQUE (user_id, provider),
  CONSTRAINT fk_user_tenant1 FOREIGN KEY (tenant_id)
    REFERENCES workshop.tenant (id)
);

-- Indexes for workshop.application_user
CREATE INDEX fk_user_tenant1_idx ON workshop.application_user (tenant_id);

-- -----------------------------------------------------
-- Table workshop.work_order_detail
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS workshop.work_order_detail (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  work_order_id VARCHAR(50) NOT NULL,
  product_id VARCHAR(50) NOT NULL,
  invoice_detail_id VARCHAR(50) DEFAULT NULL,
  quantity DOUBLE PRECISION NOT NULL DEFAULT 0,
  unit_price DOUBLE PRECISION NOT NULL DEFAULT 0,
  tenant VARCHAR(50) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (work_order_id, product_id, tenant),
  CONSTRAINT fk_work_order_detail_invoice_detail1 FOREIGN KEY (invoice_detail_id)
    REFERENCES workshop.invoice_detail (id),
  CONSTRAINT fk_work_order_detail_product1 FOREIGN KEY (product_id)
    REFERENCES workshop.product (id),
  CONSTRAINT fk_work_order_detail_work_order1 FOREIGN KEY (work_order_id)
    REFERENCES workshop.work_order (id)
);

-- Indexes for workshop.work_order_detail
CREATE INDEX fk_work_order_detail_work_order1_idx ON workshop.work_order_detail (work_order_id);
CREATE INDEX fk_work_order_detail_product1_idx ON workshop.work_order_detail (product_id);
CREATE INDEX idx_work_order_detail_tenant ON workshop.work_order_detail (tenant);
CREATE INDEX fk_work_order_detail_invoice_detail1_idx ON workshop.work_order_detail (invoice_detail_id);
