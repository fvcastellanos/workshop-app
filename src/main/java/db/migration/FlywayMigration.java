package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

abstract class FlywayMigration extends BaseJavaMigration {

    protected JdbcTemplate getJdbcTemplate(final Context context) throws Exception{

        final var connection = new SingleConnectionDataSource(context.getConnection(), true);
        return new JdbcTemplate(connection);
    }
}
