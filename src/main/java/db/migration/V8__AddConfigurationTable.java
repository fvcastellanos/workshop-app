package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V8__AddConfigurationTable  extends FlywayMigration {


    @Override
    public void migrate(Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                CREATE TABLE workshop.configuration (
                    id character varying(50) NOT NULL,
                    key character varying(100) NOT NULL,
                    value text,
                    tenant character varying(50) NOT NULL,
                    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated timestamp,
                    CONSTRAINT application_configuration_pk PRIMARY KEY (id)
                );

                ALTER TABLE workshop.configuration OWNER TO workshop;

                CREATE UNIQUE INDEX uq_configuration_key ON workshop.configuration
                USING btree
                (
                    key,
                    tenant
                );                
            """);
    }
    
}
