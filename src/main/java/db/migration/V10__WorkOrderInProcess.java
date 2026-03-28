package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V10__WorkOrderInProcess extends FlywayMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                ALTER TABLE workshop.work_order_detail \
                ADD operation_date timestamp DEFAULT CURRENT_TIMESTAMP NULL;
                
                ALTER TABLE workshop.work_order ADD color varchar(50) NULL;
                ALTER TABLE workshop.work_order ADD make_year smallint NULL;
                ALTER TABLE workshop.work_order ADD close_date timestamp DEFAULT CURRENT_TIMESTAMP NULL;
                """);
    }
}
