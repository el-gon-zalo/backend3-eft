package cl.duoc.pby2203.week8.mf.repository;

import cl.duoc.pby2203.week8.mf.domain.MovimientoFinanciero;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/** Lee y escribe la tabla movimientoFinanciero_processed, la misma que llena translog-batch. */
@Repository
public class MovimientoFinancieroRepository {

    private final MovimientoFinancieroJpaRepository jpa;

    public MovimientoFinancieroRepository(MovimientoFinancieroJpaRepository jpa) {
        this.jpa = jpa;
    }

    public MovimientoFinanciero save(MovimientoFinanciero movimientoFinanciero) {
        return jpa.save(movimientoFinanciero);
    }

    public Optional<MovimientoFinanciero> findById(String id) {
        try {
            return jpa.findById(Integer.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Collection<MovimientoFinanciero> findAll() {
        return jpa.findAll();
    }
}