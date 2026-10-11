package cl.translog.batch.service;

import cl.translog.batch.domain.EstadoFinanciero;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoFinancieroService {

    private final JdbcTemplate jdbcTemplate;

    public EstadoFinancieroService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET por ID
    public EstadoFinanciero obtenerPorId(Long id) {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    fecha,
                    transaccion,
                    monto,
                    descripcion
                FROM estadoFinanciero_processed
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    EstadoFinanciero cuenta = new EstadoFinanciero();

                    cuenta.setId(rs.getLong("id"));
                    cuenta.setCuentaId(rs.getInt("cuenta_id"));
                    cuenta.setFecha(rs.getString("fecha"));
                    cuenta.setTransaccion(rs.getString("transaccion"));
                    cuenta.setMonto(rs.getDouble("monto"));
                    cuenta.setDescripcion(rs.getString("descripcion"));

                    return cuenta;
                },
                id
        );
    }

    // GET ALL
    public List<EstadoFinanciero> obtenerTodos() {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    fecha,
                    transaccion,
                    monto,
                    descripcion
                FROM estadoFinanciero_processed
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    EstadoFinanciero cuenta = new EstadoFinanciero();

                    cuenta.setId(rs.getLong("id"));
                    cuenta.setCuentaId(rs.getInt("cuenta_id"));
                    cuenta.setFecha(rs.getString("fecha"));
                    cuenta.setTransaccion(rs.getString("transaccion"));
                    cuenta.setMonto(rs.getDouble("monto"));
                    cuenta.setDescripcion(rs.getString("descripcion"));

                    return cuenta;
                }
        );
    }

    
    public List<EstadoFinanciero> obtenerPorCuentaId(Integer cuentaId) {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    fecha,
                    transaccion,
                    monto,
                    descripcion
                FROM estadoFinanciero_processed
                WHERE cuenta_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    EstadoFinanciero cuenta = new EstadoFinanciero();

                    cuenta.setId(rs.getLong("id"));
                    cuenta.setCuentaId(rs.getInt("cuenta_id"));
                    cuenta.setFecha(rs.getString("fecha"));
                    cuenta.setTransaccion(rs.getString("transaccion"));
                    cuenta.setMonto(rs.getDouble("monto"));
                    cuenta.setDescripcion(rs.getString("descripcion"));

                    return cuenta;
                },
                cuentaId
        );
    }
}
