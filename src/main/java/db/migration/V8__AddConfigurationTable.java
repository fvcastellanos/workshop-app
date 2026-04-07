package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V8__AddConfigurationTable  extends FlywayMigration {


    @Override
    public void migrate(Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                CREATE TABLE configuration (
                    id character varying(50) NOT NULL,
                    key character varying(100) NOT NULL,
                    value text,
                    tenant character varying(50) NOT NULL,
                    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated timestamp,
                    CONSTRAINT application_configuration_pk PRIMARY KEY (id)
                );

                ALTER TABLE configuration \
                ADD CONSTRAINT uq_configuration_key UNIQUE (key, tenant);
            """);
    }
}
