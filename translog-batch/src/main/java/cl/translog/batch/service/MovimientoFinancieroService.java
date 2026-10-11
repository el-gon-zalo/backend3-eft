package cl.translog.batch.service;

import cl.translog.batch.domain.MovimientoFinanciero;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoFinancieroService {

    private final JdbcTemplate jdbcTemplate;

    public MovimientoFinancieroService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET por ID
    public MovimientoFinanciero obtenerPorId(Long id) {

        String sql = """
                SELECT
                    id,
                    fecha,
                    monto,
                    tipo,
                    estado
                FROM movimientoFinanciero_processed
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    MovimientoFinanciero movimientoFinanciero = new MovimientoFinanciero();

                    movimientoFinanciero.setId(rs.getInt("id"));
                    movimientoFinanciero.setFecha(rs.getString("fecha"));
                    movimientoFinanciero.setMonto(rs.getDouble("monto"));
                    movimientoFinanciero.setTipo(rs.getString("tipo"));
                    movimientoFinanciero.setEstado(rs.getString("estado"));

                    return movimientoFinanciero;
                },
                id
        );
    }

    // GET ALL
    public List<MovimientoFinanciero> obtenerTodos() {

        String sql = """
                SELECT
                     id,
                    fecha,
                    monto,
                    tipo,
                    estado
                FROM movimientoFinanciero_processed
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    MovimientoFinanciero movimientoFinanciero = new MovimientoFinanciero();

                    movimientoFinanciero.setId(rs.getInt("id"));
                    movimientoFinanciero.setFecha(rs.getString("fecha"));
                    movimientoFinanciero.setMonto(rs.getDouble("monto"));
                    movimientoFinanciero.setTipo(rs.getString("tipo"));
                    movimientoFinanciero.setEstado(rs.getString("estado"));

                    return movimientoFinanciero;
                }
        );
    }

}
