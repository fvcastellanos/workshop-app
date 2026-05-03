package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V12__UpdateMovementsConstraints extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
                ALTER TABLE workshop.inventory DROP CONSTRAINT inventory_product_id_invoice_detail_id_tenant_operation_typ_key;
                ALTER TABLE workshop.invoice_detail DROP CONSTRAINT invoice_detail_invoice_id_product_id_tenant_key;
                """);
    }
}
