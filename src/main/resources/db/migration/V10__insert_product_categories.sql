insert into product_category
(id, code, name, tenant)
values
(generate_ksuid(), '001', 'LLANTAS', 'resta'),
(generate_ksuid(), '002', 'REPUESTOS', 'resta'),
(generate_ksuid(), '003', 'ACEITES Y GRASAS', 'resta'),
(generate_ksuid(), '004', 'LUBRICANTES', 'resta'),
(generate_ksuid(), '005', 'BATERIAS', 'resta'),
(generate_ksuid(), '006', 'TALLERES AJENOS', 'resta'),

(generate_ksuid(), '001', 'LLANTAS', 'test'),
(generate_ksuid(), '002', 'REPUESTOS', 'test'),
(generate_ksuid(), '003', 'ACEITES Y GRASAS', 'test'),
(generate_ksuid(), '004', 'LUBRICANTES', 'test'),
(generate_ksuid(), '005', 'BATERIAS', 'test'),
(generate_ksuid(), '006', 'TALLERES AJENOS', 'test')
;
