-- object: workshop.car_brand | type: TABLE --
-- DROP TABLE IF EXISTS workshop.car_brand CASCADE;
CREATE TABLE workshop.car_brand (
	id character varying(50) NOT NULL,
	name character varying(100) NOT NULL,
	description character varying(300) DEFAULT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	active integer NOT NULL DEFAULT 1,
	tenant character varying(50) NOT NULL,
	CONSTRAINT car_brand_pkey PRIMARY KEY (id),
	CONSTRAINT car_brand_name_tenant_key UNIQUE (name,tenant)
);
-- ddl-end --
ALTER TABLE workshop.car_brand OWNER TO workshop;
-- ddl-end --

-- object: idx_car_brand_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_car_brand_created CASCADE;
CREATE INDEX idx_car_brand_created ON workshop.car_brand
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_car_brand_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_car_brand_active CASCADE;
CREATE INDEX idx_car_brand_active ON workshop.car_brand
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_car_brand_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_car_brand_tenant CASCADE;
CREATE INDEX idx_car_brand_tenant ON workshop.car_brand
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.car_line | type: TABLE --
-- DROP TABLE IF EXISTS workshop.car_line CASCADE;
CREATE TABLE workshop.car_line (
	id character varying(50) NOT NULL,
	car_brand_id character varying(50) NOT NULL,
	name character varying(100) NOT NULL,
	description character varying(300) DEFAULT NULL,
	active integer NOT NULL DEFAULT 1,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	tenant character varying(50) NOT NULL,
	CONSTRAINT car_line_pkey PRIMARY KEY (id),
	CONSTRAINT car_line_name_tenant_key UNIQUE (name,tenant)
);
-- ddl-end --
ALTER TABLE workshop.car_line OWNER TO workshop;
-- ddl-end --

-- object: idx_car_line_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_car_line_created CASCADE;
CREATE INDEX idx_car_line_created ON workshop.car_line
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_car_line_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_car_line_active CASCADE;
CREATE INDEX idx_car_line_active ON workshop.car_line
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_car_line_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_car_line_tenant CASCADE;
CREATE INDEX idx_car_line_tenant ON workshop.car_line
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.contact | type: TABLE --
-- DROP TABLE IF EXISTS workshop.contact CASCADE;
CREATE TABLE workshop.contact (
	id character varying(50) NOT NULL,
	type character(1) NOT NULL DEFAULT 'C'::bpchar,
	code character varying(50) NOT NULL,
	name character varying(150) NOT NULL,
	contact character varying(150) DEFAULT NULL,
	tax_id character varying(50) DEFAULT NULL,
	description character varying(300) DEFAULT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	active integer NOT NULL DEFAULT 1,
	tenant character varying(50) NOT NULL,
	CONSTRAINT contact_pkey PRIMARY KEY (id),
	CONSTRAINT contact_code_tenant_key UNIQUE (code,tenant)
);
-- ddl-end --
ALTER TABLE workshop.contact OWNER TO workshop;
-- ddl-end --

-- object: idx_contact_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contact_created CASCADE;
CREATE INDEX idx_contact_created ON workshop.contact
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_contact_tax_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contact_tax_id CASCADE;
CREATE INDEX idx_contact_tax_id ON workshop.contact
USING btree
(
	tax_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_contact_updated | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contact_updated CASCADE;
CREATE INDEX idx_contact_updated ON workshop.contact
USING btree
(
	updated
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_contact_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contact_tenant CASCADE;
CREATE INDEX idx_contact_tenant ON workshop.contact
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_contact_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contact_active CASCADE;
CREATE INDEX idx_contact_active ON workshop.contact
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_contat_type | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contat_type CASCADE;
CREATE INDEX idx_contat_type ON workshop.contact
USING btree
(
	type
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_contact_name | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_contact_name CASCADE;
CREATE INDEX idx_contact_name ON workshop.contact
USING btree
(
	name
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.discount_type | type: TABLE --
-- DROP TABLE IF EXISTS workshop.discount_type CASCADE;
CREATE TABLE workshop.discount_type (
	id character varying(50) NOT NULL,
	name character varying(150) NOT NULL,
	description character varying(300) DEFAULT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	active integer NOT NULL DEFAULT 1,
	tenant character varying(50) NOT NULL,
	CONSTRAINT discount_type_pkey PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE workshop.discount_type OWNER TO workshop;
-- ddl-end --

-- object: idx_discount_type_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_discount_type_created CASCADE;
CREATE INDEX idx_discount_type_created ON workshop.discount_type
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_discount_type_updated | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_discount_type_updated CASCADE;
CREATE INDEX idx_discount_type_updated ON workshop.discount_type
USING btree
(
	updated
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_discount_type_tenantid | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_discount_type_tenantid CASCADE;
CREATE INDEX idx_discount_type_tenantid ON workshop.discount_type
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_discount_type_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_discount_type_active CASCADE;
CREATE INDEX idx_discount_type_active ON workshop.discount_type
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.operation_type | type: TABLE --
-- DROP TABLE IF EXISTS workshop.operation_type CASCADE;
CREATE TABLE workshop.operation_type (
	id character varying(50) NOT NULL,
	type character(1) NOT NULL DEFAULT 'I'::bpchar,
	name character varying(150) NOT NULL,
	description character varying(300) DEFAULT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	active integer NOT NULL DEFAULT 1,
	tenant character varying(50) NOT NULL,
	code character varying(50) NOT NULL,
	CONSTRAINT operation_type_pkey PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE workshop.operation_type OWNER TO workshop;
-- ddl-end --

-- object: idx_operation_type_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_operation_type_created CASCADE;
CREATE INDEX idx_operation_type_created ON workshop.operation_type
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_operation_type_updated | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_operation_type_updated CASCADE;
CREATE INDEX idx_operation_type_updated ON workshop.operation_type
USING btree
(
	updated
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_operation_type_tenant_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_operation_type_tenant_id CASCADE;
CREATE INDEX idx_operation_type_tenant_id ON workshop.operation_type
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_operation_type_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_operation_type_active CASCADE;
CREATE INDEX idx_operation_type_active ON workshop.operation_type
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_operation_type_type | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_operation_type_type CASCADE;
CREATE INDEX idx_operation_type_type ON workshop.operation_type
USING btree
(
	type
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: operation_type_code_idx | type: INDEX --
-- DROP INDEX IF EXISTS workshop.operation_type_code_idx CASCADE;
CREATE INDEX operation_type_code_idx ON workshop.operation_type
USING btree
(
	code
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.product_category | type: TABLE --
-- DROP TABLE IF EXISTS workshop.product_category CASCADE;
CREATE TABLE workshop.product_category (
	id character varying(50) NOT NULL,
	sequence_id character varying(50),
	code character varying(50) NOT NULL,
	name character varying(150) NOT NULL,
	description character varying(300) DEFAULT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp,
	active integer NOT NULL DEFAULT 1,
	tenant character varying(50) NOT NULL,
	CONSTRAINT product_category_pkey PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE workshop.product_category OWNER TO workshop;
-- ddl-end --

-- object: idx_product_category_code | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_category_code CASCADE;
CREATE INDEX idx_product_category_code ON workshop.product_category
USING btree
(
	code
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_product_category_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_category_tenant CASCADE;
CREATE INDEX idx_product_category_tenant ON workshop.product_category
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_product_category_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_category_active CASCADE;
CREATE INDEX idx_product_category_active ON workshop.product_category
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.product | type: TABLE --
-- DROP TABLE IF EXISTS workshop.product CASCADE;
CREATE TABLE workshop.product (
	id character varying(50) NOT NULL,
	product_category_id character varying(50) DEFAULT NULL,
	storable boolean NOT NULL DEFAULT true,
	code character varying(50) NOT NULL,
	name character varying(150) NOT NULL,
	description character varying(300) DEFAULT NULL,
	minimal_quantity double precision NOT NULL DEFAULT 0,
	sale_price double precision NOT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp DEFAULT CURRENT_TIMESTAMP,
	active integer NOT NULL DEFAULT 1,
	tenant character varying(50) NOT NULL,
	CONSTRAINT product_pkey PRIMARY KEY (id),
	CONSTRAINT product_code_tenant_key UNIQUE (code,tenant)
);
-- ddl-end --
ALTER TABLE workshop.product OWNER TO workshop;
-- ddl-end --

-- object: idx_product_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_created CASCADE;
CREATE INDEX idx_product_created ON workshop.product
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_product_updated | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_updated CASCADE;
CREATE INDEX idx_product_updated ON workshop.product
USING btree
(
	updated
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_product_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_tenant CASCADE;
CREATE INDEX idx_product_tenant ON workshop.product
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_product_active | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_active CASCADE;
CREATE INDEX idx_product_active ON workshop.product
USING btree
(
	active
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.inventory | type: TABLE --
-- DROP TABLE IF EXISTS workshop.inventory CASCADE;
CREATE TABLE workshop.inventory (
	id character varying(50) NOT NULL,
	product_id character varying(50) NOT NULL,
	invoice_detail_id character varying(50) DEFAULT NULL,
	quantity double precision NOT NULL DEFAULT 1,
	unit_price double precision NOT NULL DEFAULT 0,
	discount_amount double precision NOT NULL DEFAULT 0,
	total double precision NOT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	description character varying(200) DEFAULT NULL,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	tenant character varying(50) NOT NULL,
	operation_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	operation_type_id character varying(50) NOT NULL,
	CONSTRAINT inventory_pkey PRIMARY KEY (id),
	CONSTRAINT inventory_product_id_invoice_detail_id_tenant_operation_typ_key UNIQUE (product_id,invoice_detail_id,tenant,operation_type_id)
);
-- ddl-end --
ALTER TABLE workshop.inventory OWNER TO workshop;
-- ddl-end --

-- object: idx_inventory_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_inventory_created CASCADE;
CREATE INDEX idx_inventory_created ON workshop.inventory
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_inventory_updated | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_inventory_updated CASCADE;
CREATE INDEX idx_inventory_updated ON workshop.inventory
USING btree
(
	updated
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_inventory_tenant_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_inventory_tenant_id CASCADE;
CREATE INDEX idx_inventory_tenant_id ON workshop.inventory
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_inventory_product_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_inventory_product_id CASCADE;
CREATE INDEX idx_inventory_product_id ON workshop.inventory
USING btree
(
	product_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_inventory_operation_date | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_inventory_operation_date CASCADE;
CREATE INDEX idx_inventory_operation_date ON workshop.inventory
USING btree
(
	operation_date
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_inventory_operation_type_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_inventory_operation_type_id CASCADE;
CREATE INDEX idx_inventory_operation_type_id ON workshop.inventory
USING btree
(
	operation_type_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.invoice | type: TABLE --
-- DROP TABLE IF EXISTS workshop.invoice CASCADE;
CREATE TABLE workshop.invoice (
	id character varying(50) NOT NULL,
	contact_id character varying(50) NOT NULL,
	type character varying(45) NOT NULL DEFAULT 'P',
	suffix character varying(30) DEFAULT NULL,
	number character varying(100) NOT NULL,
	image_url character varying(250) DEFAULT NULL,
	invoice_date timestamp NOT NULL,
	effective_date timestamp,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	tenant character varying(50) NOT NULL,
	status character(1) NOT NULL DEFAULT 'A'::bpchar,
	CONSTRAINT invoice_pkey PRIMARY KEY (id),
	CONSTRAINT invoice_suffix_number_tenant_type_contact_id_key UNIQUE (suffix,number,tenant,type,contact_id)
);
-- ddl-end --
ALTER TABLE workshop.invoice OWNER TO workshop;
-- ddl-end --

-- object: idx_invoice_created | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_created CASCADE;
CREATE INDEX idx_invoice_created ON workshop.invoice
USING btree
(
	created
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_updated | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_updated CASCADE;
CREATE INDEX idx_invoice_updated ON workshop.invoice
USING btree
(
	updated
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_tenant CASCADE;
CREATE INDEX idx_invoice_tenant ON workshop.invoice
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_contact_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_contact_id CASCADE;
CREATE INDEX idx_invoice_contact_id ON workshop.invoice
USING btree
(
	contact_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_type | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_type CASCADE;
CREATE INDEX idx_invoice_type ON workshop.invoice
USING btree
(
	type
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_date | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_date CASCADE;
CREATE INDEX idx_invoice_date ON workshop.invoice
USING btree
(
	invoice_date
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_effective_date | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_effective_date CASCADE;
CREATE INDEX idx_invoice_effective_date ON workshop.invoice
USING btree
(
	effective_date
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_status | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_status CASCADE;
CREATE INDEX idx_invoice_status ON workshop.invoice
USING btree
(
	status
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.work_order | type: TABLE --
-- DROP TABLE IF EXISTS workshop.work_order CASCADE;
CREATE TABLE workshop.work_order (
	id character varying(50) NOT NULL,
	car_line_id character varying(50) NOT NULL,
	contact_id character varying(50) NOT NULL,
	number character varying(100) NOT NULL,
	status character(1) NOT NULL DEFAULT 'P'::bpchar,
	plate_number character varying(30) NOT NULL,
	odometer_measurement character(1) NOT NULL DEFAULT 'K'::bpchar,
	odometer_value double precision NOT NULL,
	gas_amount double precision NOT NULL DEFAULT 0,
	tenant character varying(50) NOT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	notes text,
	order_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT work_order_pkey PRIMARY KEY (id),
	CONSTRAINT work_order_number_tenant_key UNIQUE (number,tenant)
);
-- ddl-end --
ALTER TABLE workshop.work_order OWNER TO workshop;
-- ddl-end --

-- object: idx_work_order_car_line_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_work_order_car_line_id CASCADE;
CREATE INDEX idx_work_order_car_line_id ON workshop.work_order
USING btree
(
	car_line_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_work_order_contact_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_work_order_contact_id CASCADE;
CREATE INDEX idx_work_order_contact_id ON workshop.work_order
USING btree
(
	contact_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_work_order_plate_number | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_work_order_plate_number CASCADE;
CREATE INDEX idx_work_order_plate_number ON workshop.work_order
USING btree
(
	plate_number
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_work_order_order_date | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_work_order_order_date CASCADE;
CREATE INDEX idx_work_order_order_date ON workshop.work_order
USING btree
(
	order_date
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.invoice_detail | type: TABLE --
-- DROP TABLE IF EXISTS workshop.invoice_detail CASCADE;
CREATE TABLE workshop.invoice_detail (
	id character varying(50) NOT NULL,
	invoice_id character varying(50) NOT NULL,
	product_id character varying(50) NOT NULL,
	work_order_id character varying(50) DEFAULT NULL,
	quantity double precision NOT NULL DEFAULT 1,
	unit_price double precision NOT NULL DEFAULT 0,
	discount_amount double precision NOT NULL DEFAULT 0,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	tenant character varying(50) NOT NULL,
	CONSTRAINT invoice_detail_pkey PRIMARY KEY (id),
	CONSTRAINT invoice_detail_invoice_id_product_id_tenant_key UNIQUE (invoice_id,product_id,tenant)
);
-- ddl-end --
ALTER TABLE workshop.invoice_detail OWNER TO workshop;
-- ddl-end --

-- object: idx_invoice_detail_invoice_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_detail_invoice_id CASCADE;
CREATE INDEX idx_invoice_detail_invoice_id ON workshop.invoice_detail
USING btree
(
	invoice_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_detail_product_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_detail_product_id CASCADE;
CREATE INDEX idx_invoice_detail_product_id ON workshop.invoice_detail
USING btree
(
	product_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_invoice_detail_work_order_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_invoice_detail_work_order_id CASCADE;
CREATE INDEX idx_invoice_detail_work_order_id ON workshop.invoice_detail
USING btree
(
	work_order_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.sequence | type: TABLE --
-- DROP TABLE IF EXISTS workshop.sequence CASCADE;
CREATE TABLE workshop.sequence (
	id character varying(50) NOT NULL,
	prefix character varying(5) NOT NULL,
	value integer NOT NULL DEFAULT 1,
	description character varying(300) DEFAULT NULL,
	pad_size integer NOT NULL DEFAULT 3,
	step_size integer NOT NULL DEFAULT 1,
	tenant character varying(50) DEFAULT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp,
	CONSTRAINT sequence_pkey PRIMARY KEY (id),
	CONSTRAINT sequence_prefix_tenant_key UNIQUE (prefix,tenant)
);
-- ddl-end --
ALTER TABLE workshop.sequence OWNER TO workshop;
-- ddl-end --

-- object: idx_sequence_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_sequence_tenant CASCADE;
CREATE INDEX idx_sequence_tenant ON workshop.sequence
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.tenant | type: TABLE --
-- DROP TABLE IF EXISTS workshop.tenant CASCADE;
CREATE TABLE workshop.tenant (
	id character varying(50) NOT NULL,
	code character varying(50) NOT NULL,
	name character varying(150) NOT NULL,
	active integer NOT NULL DEFAULT 1,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT tenant_pkey PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE workshop.tenant OWNER TO workshop;
-- ddl-end --

-- object: idx_tenant_code | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_tenant_code CASCADE;
CREATE INDEX idx_tenant_code ON workshop.tenant
USING btree
(
	code
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.application_user | type: TABLE --
-- DROP TABLE IF EXISTS workshop.application_user CASCADE;
CREATE TABLE workshop.application_user (
	id character varying(50) NOT NULL,
	tenant_id character varying(50) NOT NULL,
	provider character varying(50) NOT NULL,
	user_id character varying(150) NOT NULL,
	active integer NOT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated timestamp,
	CONSTRAINT application_user_pkey PRIMARY KEY (id),
	CONSTRAINT application_user_user_id_provider_key UNIQUE (user_id,provider)
);
-- ddl-end --
ALTER TABLE workshop.application_user OWNER TO workshop;
-- ddl-end --

-- object: fk_user_tenant1_idx | type: INDEX --
-- DROP INDEX IF EXISTS workshop.fk_user_tenant1_idx CASCADE;
CREATE INDEX fk_user_tenant1_idx ON workshop.application_user
USING btree
(
	tenant_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.work_order_detail | type: TABLE --
-- DROP TABLE IF EXISTS workshop.work_order_detail CASCADE;
CREATE TABLE workshop.work_order_detail (
	id character varying(50) NOT NULL,
	work_order_id character varying(50) NOT NULL,
	product_id character varying(50) NOT NULL,
	invoice_detail_id character varying(50) DEFAULT NULL,
	quantity double precision NOT NULL DEFAULT 0,
	unit_price double precision NOT NULL DEFAULT 0,
	tenant character varying(50) NOT NULL,
	created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT work_order_detail_pkey PRIMARY KEY (id),
	CONSTRAINT work_order_detail_work_order_id_product_id_tenant_key UNIQUE (work_order_id,product_id,tenant)
);
-- ddl-end --
ALTER TABLE workshop.work_order_detail OWNER TO workshop;
-- ddl-end --

-- object: fk_work_order_detail_work_order1_idx | type: INDEX --
-- DROP INDEX IF EXISTS workshop.fk_work_order_detail_work_order1_idx CASCADE;
CREATE INDEX fk_work_order_detail_work_order1_idx ON workshop.work_order_detail
USING btree
(
	work_order_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: fk_work_order_detail_product1_idx | type: INDEX --
-- DROP INDEX IF EXISTS workshop.fk_work_order_detail_product1_idx CASCADE;
CREATE INDEX fk_work_order_detail_product1_idx ON workshop.work_order_detail
USING btree
(
	product_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: idx_work_order_detail_tenant | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_work_order_detail_tenant CASCADE;
CREATE INDEX idx_work_order_detail_tenant ON workshop.work_order_detail
USING btree
(
	tenant
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: fk_work_order_detail_invoice_detail1_idx | type: INDEX --
-- DROP INDEX IF EXISTS workshop.fk_work_order_detail_invoice_detail1_idx CASCADE;
CREATE INDEX fk_work_order_detail_invoice_detail1_idx ON workshop.work_order_detail
USING btree
(
	invoice_detail_id
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: workshop.product_stock | type: VIEW --
-- DROP VIEW IF EXISTS workshop.product_stock CASCADE;
CREATE VIEW workshop.product_stock
AS
SELECT sum(value) AS total,
    tenant,
    name,
    code,
    product_id
   FROM ( SELECT
                CASE
                    WHEN (movements.type = 'I'::bpchar) THEN movements.sum_quantity
                    WHEN (movements.type = 'O'::bpchar) THEN (movements.sum_quantity * ('-1'::integer)::double precision)
                    ELSE (0)::double precision
                END AS value,
            movements.tenant,
            movements.name,
            movements.code,
            movements.id AS product_id
           FROM ( SELECT i.tenant,
                    sum(i.quantity) AS sum_quantity,
                    p.name,
                    p.code,
                    p.id,
                    ot.type
                   FROM ((inventory i
                     JOIN product p ON (((i.product_id)::text = (p.id)::text)))
                     JOIN operation_type ot ON (((i.operation_type_id)::text = (ot.id)::text)))
                  GROUP BY i.tenant, ot.type, p.name, p.code, p.id) movements) stock
  GROUP BY tenant, name, code, product_id;
-- ddl-end --
ALTER VIEW workshop.product_stock OWNER TO workshop;
-- ddl-end --

-- object: idx_product_category_sequence_id | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_category_sequence_id CASCADE;
CREATE INDEX idx_product_category_sequence_id ON workshop.product_category
USING btree
(
	sequence_id
);
-- ddl-end --

-- object: idx_product_storable | type: INDEX --
-- DROP INDEX IF EXISTS workshop.idx_product_storable CASCADE;
CREATE INDEX idx_product_storable ON workshop.product
USING btree
(
	storable
);
-- ddl-end --

-- object: fk_car_line_car_brand1 | type: CONSTRAINT --
-- ALTER TABLE workshop.car_line DROP CONSTRAINT IF EXISTS fk_car_line_car_brand1 CASCADE;
ALTER TABLE workshop.car_line ADD CONSTRAINT fk_car_line_car_brand1 FOREIGN KEY (car_brand_id)
REFERENCES workshop.car_brand (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_product_category_sequence_id | type: CONSTRAINT --
-- ALTER TABLE workshop.product_category DROP CONSTRAINT IF EXISTS fk_product_category_sequence_id CASCADE;
ALTER TABLE workshop.product_category ADD CONSTRAINT fk_product_category_sequence_id FOREIGN KEY (sequence_id)
REFERENCES workshop.sequence (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: product_product_category_id_fk | type: CONSTRAINT --
-- ALTER TABLE workshop.product DROP CONSTRAINT IF EXISTS product_product_category_id_fk CASCADE;
ALTER TABLE workshop.product ADD CONSTRAINT product_product_category_id_fk FOREIGN KEY (product_category_id)
REFERENCES workshop.product_category (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_inventory_operation_type | type: CONSTRAINT --
-- ALTER TABLE workshop.inventory DROP CONSTRAINT IF EXISTS fk_inventory_operation_type CASCADE;
ALTER TABLE workshop.inventory ADD CONSTRAINT fk_inventory_operation_type FOREIGN KEY (operation_type_id)
REFERENCES workshop.operation_type (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_inventory_product | type: CONSTRAINT --
-- ALTER TABLE workshop.inventory DROP CONSTRAINT IF EXISTS fk_inventory_product CASCADE;
ALTER TABLE workshop.inventory ADD CONSTRAINT fk_inventory_product FOREIGN KEY (product_id)
REFERENCES workshop.product (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_invoice_contact | type: CONSTRAINT --
-- ALTER TABLE workshop.invoice DROP CONSTRAINT IF EXISTS fk_invoice_contact CASCADE;
ALTER TABLE workshop.invoice ADD CONSTRAINT fk_invoice_contact FOREIGN KEY (contact_id)
REFERENCES workshop.contact (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_work_order_car_line | type: CONSTRAINT --
-- ALTER TABLE workshop.work_order DROP CONSTRAINT IF EXISTS fk_work_order_car_line CASCADE;
ALTER TABLE workshop.work_order ADD CONSTRAINT fk_work_order_car_line FOREIGN KEY (car_line_id)
REFERENCES workshop.car_line (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_work_order_contact | type: CONSTRAINT --
-- ALTER TABLE workshop.work_order DROP CONSTRAINT IF EXISTS fk_work_order_contact CASCADE;
ALTER TABLE workshop.work_order ADD CONSTRAINT fk_work_order_contact FOREIGN KEY (contact_id)
REFERENCES workshop.contact (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_invoice_detail_invoice | type: CONSTRAINT --
-- ALTER TABLE workshop.invoice_detail DROP CONSTRAINT IF EXISTS fk_invoice_detail_invoice CASCADE;
ALTER TABLE workshop.invoice_detail ADD CONSTRAINT fk_invoice_detail_invoice FOREIGN KEY (invoice_id)
REFERENCES workshop.invoice (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_invoice_detail_product | type: CONSTRAINT --
-- ALTER TABLE workshop.invoice_detail DROP CONSTRAINT IF EXISTS fk_invoice_detail_product CASCADE;
ALTER TABLE workshop.invoice_detail ADD CONSTRAINT fk_invoice_detail_product FOREIGN KEY (product_id)
REFERENCES workshop.product (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_invoice_detail_work_order | type: CONSTRAINT --
-- ALTER TABLE workshop.invoice_detail DROP CONSTRAINT IF EXISTS fk_invoice_detail_work_order CASCADE;
ALTER TABLE workshop.invoice_detail ADD CONSTRAINT fk_invoice_detail_work_order FOREIGN KEY (work_order_id)
REFERENCES workshop.work_order (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_user_tenant1 | type: CONSTRAINT --
-- ALTER TABLE workshop.application_user DROP CONSTRAINT IF EXISTS fk_user_tenant1 CASCADE;
ALTER TABLE workshop.application_user ADD CONSTRAINT fk_user_tenant1 FOREIGN KEY (tenant_id)
REFERENCES workshop.tenant (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_work_order_detail_invoice_detail1 | type: CONSTRAINT --
-- ALTER TABLE workshop.work_order_detail DROP CONSTRAINT IF EXISTS fk_work_order_detail_invoice_detail1 CASCADE;
ALTER TABLE workshop.work_order_detail ADD CONSTRAINT fk_work_order_detail_invoice_detail1 FOREIGN KEY (invoice_detail_id)
REFERENCES workshop.invoice_detail (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_work_order_detail_product1 | type: CONSTRAINT --
-- ALTER TABLE workshop.work_order_detail DROP CONSTRAINT IF EXISTS fk_work_order_detail_product1 CASCADE;
ALTER TABLE workshop.work_order_detail ADD CONSTRAINT fk_work_order_detail_product1 FOREIGN KEY (product_id)
REFERENCES workshop.product (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_work_order_detail_work_order1 | type: CONSTRAINT --
-- ALTER TABLE workshop.work_order_detail DROP CONSTRAINT IF EXISTS fk_work_order_detail_work_order1 CASCADE;
ALTER TABLE workshop.work_order_detail ADD CONSTRAINT fk_work_order_detail_work_order1 FOREIGN KEY (work_order_id)
REFERENCES workshop.work_order (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

