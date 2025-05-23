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
                    ('%s', 'I', 'MI01', 'INVENTARIO INICIAL', '', 1, 'resta'),
                    ('%s', 'I', 'MI02', 'IMPORTACION', '', 1, 'resta'),
                    ('%s', 'I', 'MI03', 'COMPRAS LOCALES', '', 1, 'resta'),
                    ('%s', 'I', 'MI04', 'DEVOLUCION', '', 1, 'resta'),
                    ('%s', 'I', 'MI05', 'AJUSTE INVENTARIO (+)', '', 1, 'resta'),
                    ('%s', 'O', 'MI06', 'AJUSTE INVENTARIO (-)', '', 1, 'resta'),
                    ('%s', 'I', 'MI07', 'NOTA TEMPORAL INGRESO', '', 1, 'resta'),
                    ('%s', 'O', 'MI08', 'NOTA TEMPORAL EGRESO', '', 1, 'resta'),
                    ('%s', 'O', 'MI09', 'VENTAS LOCALES', '', 1, 'resta')
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
                    insert into sequence
                    (id, prefix, value, tenant, description, pad_size)
                    values
                    ('%s', 'MI', 10, 'resta', 'Tipo Mov. Inv.', 2)
                """.formatted(
                        TimeBasedGenerator.generateTimeBasedId()
                    )
                );
    }
}
