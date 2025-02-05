package db.migration;

import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import org.flywaydb.core.api.migration.Context;

public class V6__ConfigureDefaultUser extends FlywayMigration {
    @Override
    public void migrate(Context context) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        final var tenantId = TimeBasedGenerator.generateTimeBasedId();
        final var userId = TimeBasedGenerator.generateTimeBasedId();

        final var sql = """
                    insert into tenant
                    (id, code, name, active)
                    values
                    ('%s', 'resta', 'Repuestos y Servicios Técnicos Automotrices', 1)
                """.formatted(tenantId);

        jdbcTemplate.execute(sql);

        final var userSql = """
                    insert into application_user
                    (id, user_id, provider, tenant_id, active)
                    values
                    ('%s', '62b2694a90d15627e3efee9e', 'auth0', '%s', 1)
                """.formatted(
                        userId,
                        tenantId
                );

        jdbcTemplate.execute(userSql);
    }
}
