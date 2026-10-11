package cl.duoc.pby2203.week8.mf.repository;

import cl.duoc.pby2203.week8.mf.domain.MovimientoFinanciero;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoFinancieroJpaRepository
        extends JpaRepository<MovimientoFinanciero, Integer> {
}