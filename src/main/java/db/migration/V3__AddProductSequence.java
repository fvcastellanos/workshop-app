package db.migration;

import org.flywaydb.core.api.migration.Context;

import net.cavitos.workshop.model.generator.TimeBasedGenerator;

public class V3__AddProductSequence extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        final var sql = """
                    insert into workshop.sequence
                    (id, prefix, tenant, description) values
                    ('%s', 'LL', 'resta', 'Llantas'),
                    ('%s', 'R', 'resta', 'Repuestos'),
                    ('%s', 'AG', 'resta', 'Aceites y Grasas'),
                    ('%s', 'L', 'resta', 'Lubricantes'),
                    ('%s', 'B', 'resta', 'Baterias'),
                    ('%s', 'TA', 'resta', 'Talleres Ajenos')
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
