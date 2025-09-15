package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V9__AddLaborSaleColumn extends FlywayMigration {

    @Override
    public void migrate(Context context) throws Exception {
        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                ALTER TABLE workshop.work_order_detail
                ADD COLUMN labor_sale boolean NOT NULL DEFAULT false;
                
                CREATE INDEX idx_labor_sale_wor_order_detail ON workshop.work_order_detail
                USING btree
                (
                	labor_sale,
                	work_order_id,
                	tenant
                );
                """);
    }
}
