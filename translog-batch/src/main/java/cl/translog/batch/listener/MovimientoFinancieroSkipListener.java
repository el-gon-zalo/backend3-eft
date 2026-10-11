package cl.translog.batch.listener;

import cl.translog.batch.domain.MovimientoFinanciero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class MovimientoFinancieroSkipListener
        implements SkipListener<MovimientoFinanciero, MovimientoFinanciero> {

    private static final Logger log =
            LoggerFactory.getLogger(MovimientoFinancieroSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(
                "[SKIP-READ] {}",
                t.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            MovimientoFinanciero item,
            Throwable t) {

        log.error(
                "[SKIP-PROCESS] movimientoFinanciero={} motivo={}",
                item.getId(),
                t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            MovimientoFinanciero item,
            Throwable t) {

        log.error(
                "[SKIP-WRITE] movimientoFinanciero={} motivo={}",
                item.getId(),
                t.getMessage()
        );
    }
}
