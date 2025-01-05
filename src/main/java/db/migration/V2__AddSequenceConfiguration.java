package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V2__AddSequenceConfiguration extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                    insert into workshop.sequence
                    (prefix, value, tenant) values
                    ('C', '1', 'resta')
                """);

        jdbcTemplate.execute("""
                    insert into workshop.sequence
                    (prefix, value, tenant) values
                    ('P', '1', 'resta')
                """);

        jdbcTemplate.execute("""
                    insert into workshop.sequence
                    (prefix, value, tenant) values
                    ('U', '1', 'resta')
                """);
    }
}
