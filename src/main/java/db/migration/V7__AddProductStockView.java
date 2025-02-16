package db.migration;

import org.flywaydb.core.api.migration.Context;

public class V7__AddProductStockView extends FlywayMigration{
    @Override
    public void migrate(Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        jdbcTemplate.execute("""
            create or replace view product_stock as
            select  sum(value) total, tenant, name, code, product_id
            from (
                select
                    CASE
                        when movements.type = 'I' then sum_quantity
                        when movements.type = 'O' then sum_quantity * (-1)
                        else 0
                    END value,
                    tenant,
                    name,
                    code,
                    id product_id
                from
                    (
                        select i.tenant, sum(i.quantity) sum_quantity, p.name, p.code, p.id, ot.type
                        from inventory i
                          inner join product p on i.product_id  = p.id
                          INNER JOIN operation_type ot on i.operation_type_id = ot.id
                        group by i.tenant, ot.type, p.name, p.code, p.id
                    ) movements
            ) stock
            group by tenant, name, code, product_id
        """);
    }
}
