insert into operation_type
(id, type, code, name, description, active, tenant)
values
(generate_ksuid(), 'I', 'MI-01', 'INVENTARIO INICIAL', '', 1, 'resta'),
(generate_ksuid(), 'I', 'MI-02', 'IMPORTACION', '', 1, 'resta'),
(generate_ksuid(), 'I', 'MI-03', 'COMPRAS LOCALES', '', 1, 'resta'),
(generate_ksuid(), 'I', 'MI-04', 'DEVOLUCION', '', 1, 'resta'),
(generate_ksuid(), 'I', 'MI-05', 'AJUSTE INVENTARIO (+)', '', 1, 'resta'),
(generate_ksuid(), 'O', 'MI-06', 'AJUSTE INVENTARIO (-)', '', 1, 'resta'),
(generate_ksuid(), 'I', 'MI-07', 'NOTA TEMPORAL INGRESO', '', 1, 'resta'),
(generate_ksuid(), 'O', 'MI-08', 'NOTA TEMPORAL EGRESO', '', 1, 'resta'),
(generate_ksuid(), 'O', 'MI-09', 'VENTAS LOCALES', '', 1, 'resta')
;

-- Update sequence
insert into `sequence`
(prefix, value)
values
('MI', '10')
;
