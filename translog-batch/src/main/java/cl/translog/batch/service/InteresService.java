package cl.translog.batch.service;

import cl.translog.batch.domain.Interes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteresService {

    private final JdbcTemplate jdbcTemplate;

    public InteresService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET por ID
    public Interes obtenerPorId(Long id) {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    nombre,
                    saldo_original,
                    edad,
                    tipo,
                    interes_aplicado,
                    saldo_final
                FROM interes_processed
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    Interes interes = new Interes();

                    interes.setId(rs.getLong("id"));
                    interes.setCuentaId(rs.getInt("cuenta_id"));
                    interes.setNombre(rs.getString("nombre"));
                    interes.setSaldoOriginal(rs.getDouble("saldo_original"));
                    interes.setEdad(rs.getInt("edad"));
                    interes.setTipo(rs.getString("tipo"));
                    interes.setInteresAplicado(rs.getDouble("interes_aplicado"));
                    interes.setSaldoFinal(rs.getDouble("saldo_final"));

                    return interes;
                },
                id
        );
    }

    // GET ALL
    public List<Interes> obtenerTodos() {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    nombre,
                    saldo_original,
                    edad,
                    tipo,
                    interes_aplicado,
                    saldo_final
                FROM interes_processed
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Interes interes = new Interes();

                    interes.setId(rs.getLong("id"));
                    interes.setCuentaId(rs.getInt("cuenta_id"));
                    interes.setNombre(rs.getString("nombre"));
                    interes.setSaldoOriginal(rs.getDouble("saldo_original"));
                    interes.setEdad(rs.getInt("edad"));
                    interes.setTipo(rs.getString("tipo"));
                    interes.setInteresAplicado(rs.getDouble("interes_aplicado"));
                    interes.setSaldoFinal(rs.getDouble("saldo_final"));

                    return interes;
                }
        );
    }

    
    public List<Interes> obtenerPorCuentaId(Integer cuentaId) {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    nombre,
                    saldo_original,
                    edad,
                    tipo,
                    interes_aplicado,
                    saldo_final
                FROM interes_processed
                WHERE cuenta_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Interes interes = new Interes();

                    interes.setId(rs.getLong("id"));
                    interes.setCuentaId(rs.getInt("cuenta_id"));
                    interes.setNombre(rs.getString("nombre"));
                    interes.setSaldoOriginal(rs.getDouble("saldo_original"));
                    interes.setEdad(rs.getInt("edad"));
                    interes.setTipo(rs.getString("tipo"));
                    interes.setInteresAplicado(rs.getDouble("interes_aplicado"));
                    interes.setSaldoFinal(rs.getDouble("saldo_final"));

                    return interes;
                },
                cuentaId
        );
    }
}
