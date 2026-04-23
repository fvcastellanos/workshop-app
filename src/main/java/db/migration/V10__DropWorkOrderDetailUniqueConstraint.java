package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V10__DropWorkOrderDetailUniqueConstraint extends FlywayMigration {

    @Override
    public void migrate(Context context) throws Exception {
        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                ALTER TABLE work_order_detail
                DROP CONSTRAINT work_order_detail_work_order_id_product_id_tenant_key;
                """);
    }
}
