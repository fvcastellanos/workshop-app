package db.migration;

import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import org.flywaydb.core.api.migration.Context;

public class V5__AddDefaultInventoryOperationType extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {
        final var jdbcTemplate = getJdbcTemplate(context);

        final var sql = """
                    insert into operation_type
                    (id, type, code, name, description, active, tenant)
                    values
                    ('%s', 'I', 'MI-01', 'INVENTARIO INICIAL', '', 1, 'resta'),
                    ('%s', 'I', 'MI-02', 'IMPORTACION', '', 1, 'resta'),
                    ('%s', 'I', 'MI-03', 'COMPRAS LOCALES', '', 1, 'resta'),
                    ('%s', 'I', 'MI-04', 'DEVOLUCION', '', 1, 'resta'),
                    ('%s', 'I', 'MI-05', 'AJUSTE INVENTARIO (+)', '', 1, 'resta'),
                    ('%s', 'O', 'MI-06', 'AJUSTE INVENTARIO (-)', '', 1, 'resta'),
                    ('%s', 'I', 'MI-07', 'NOTA TEMPORAL INGRESO', '', 1, 'resta'),
                    ('%s', 'O', 'MI-08', 'NOTA TEMPORAL EGRESO', '', 1, 'resta'),
                    ('%s', 'O', 'MI-09', 'VENTAS LOCALES', '', 1, 'resta')
                """.formatted(
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId()
                );

        jdbcTemplate.execute(sql);

        jdbcTemplate.execute("""
                    insert into `sequence`
                    (prefix, value, tenant)
                    values
                    ('MI', '10', 'tenant')
                """);
    }
}
