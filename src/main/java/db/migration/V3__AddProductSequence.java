package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V3__AddProductSequence extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                    insert into workshop.sequence
                    (prefix, value, tenant) values
                    ('PR', '1', 'resta')
                """);

        jdbcTemplate.execute("""
                    insert into workshop.sequence
                    (prefix, value, tenant) values
                    ('SR', '1', 'resta')
                """);
    }
}
