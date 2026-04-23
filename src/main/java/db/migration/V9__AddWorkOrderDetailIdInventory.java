package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V9__AddWorkOrderDetailIdInventory extends FlywayMigration {

    @Override
    public void migrate(Context context) throws Exception {
        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                ALTER TABLE inventory \
                ADD COLUMN work_order_detail_id character varying(50);
                
                ALTER TABLE inventory \
                ADD CONSTRAINT uq_inventory_work_order_detail_id UNIQUE (work_order_detail_id);
                """);
    }
}
