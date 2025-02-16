package db.migration;

import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import org.flywaydb.core.api.migration.Context;

public class V4__ConfigureDefaultProductCategory extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {
        final var jdbcTemplate = getJdbcTemplate(context);

        final var sql = """
                    insert into product_category
                    (id, code, name, tenant)
                    values
                    ('%s', '001', 'LLANTAS', 'resta'),
                    ('%s', '002', 'REPUESTOS', 'resta'),
                    ('%s', '003', 'ACEITES Y GRASAS', 'resta'),
                    ('%s', '004', 'LUBRICANTES', 'resta'),
                    ('%s', '005', 'BATERIAS', 'resta'),
                    ('%s', '006', 'TALLERES AJENOS', 'resta')
                """.formatted(
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId()
                );

        jdbcTemplate.execute(sql);
    }
}
