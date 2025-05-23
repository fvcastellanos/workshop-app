package db.migration;

import org.flywaydb.core.api.migration.Context;

import net.cavitos.workshop.model.generator.TimeBasedGenerator;

public class V2__AddSequenceConfiguration extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        final var sql = """
                insert into workshop.sequence
                (id, prefix, tenant, description) values
                ('%s', 'C', 'resta', 'Clientes'),
                ('%s', 'P', 'resta', 'Proveedores'),
                ('%s', 'U', 'resta', 'Desconocido / Generico')
                """.formatted(
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId(),
                        TimeBasedGenerator.generateTimeBasedId()
                    );

        jdbcTemplate.execute(sql);
    }
}
