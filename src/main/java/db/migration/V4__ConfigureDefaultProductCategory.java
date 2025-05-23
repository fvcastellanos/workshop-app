package db.migration;

import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import org.flywaydb.core.api.migration.Context;

public class V4__ConfigureDefaultProductCategory extends FlywayMigration {

    @Override
    public void migrate(final Context context) throws Exception {
        final var jdbcTemplate = getJdbcTemplate(context);

        var sql = """
                    insert into product_category
                    (id, code, name, tenant, sequence_id)
                    values
                    ('%s', 'CP01', 'LLANTAS', 'resta', '%s'),
                    ('%s', 'CP02', 'REPUESTOS', 'resta', '%s'),
                    ('%s', 'CP03', 'ACEITES Y GRASAS', 'resta', '%s'),
                    ('%s', 'CP04', 'LUBRICANTES', 'resta', '%s'),
                    ('%s', 'CP05', 'BATERIAS', 'resta', '%s'),
                    ('%s', 'CP06', 'TALLERES AJENOS', 'resta', '%s')
                """.formatted(
                        TimeBasedGenerator.generateTimeBasedId(),
                        getSequenceId(context, "LL"),
                        TimeBasedGenerator.generateTimeBasedId(),
                        getSequenceId(context, "R"),
                        TimeBasedGenerator.generateTimeBasedId(),
                        getSequenceId(context, "AG"),
                        TimeBasedGenerator.generateTimeBasedId(),
                        getSequenceId(context, "L"),
                        TimeBasedGenerator.generateTimeBasedId(),
                        getSequenceId(context, "B"),
                        TimeBasedGenerator.generateTimeBasedId(),
                        getSequenceId(context, "TA")
                );

        jdbcTemplate.execute(sql);

        sql = """
              insert into sequence
              (id, prefix, value, tenant, description, pad_size)
              values
              ('%s', 'CP', 7, 'resta', 'Categorias de Productos', 2)
              """.formatted(
                      TimeBasedGenerator.generateTimeBasedId()
              );

        jdbcTemplate.execute(sql);
    }

    private String getSequenceId(final Context context, final String prefix) throws Exception {

        final var jdbcTemplate = getJdbcTemplate(context);

        final var sql = """
                select id
                from workshop.sequence
                where prefix = '%s' and tenant = 'resta'
                """.formatted(prefix);

        return jdbcTemplate.queryForObject(sql, String.class);
    }
}
