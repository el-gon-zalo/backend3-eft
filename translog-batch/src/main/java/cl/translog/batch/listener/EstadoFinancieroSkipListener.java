package cl.translog.batch.listener;

import cl.translog.batch.domain.EstadoFinanciero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class EstadoFinancieroSkipListener
        implements SkipListener<EstadoFinanciero, EstadoFinanciero> {

    private static final Logger log =
            LoggerFactory.getLogger(EstadoFinancieroSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(
                "[SKIP-READ] {}",
                t.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            EstadoFinanciero item,
            Throwable t) {

        log.error(
                "[SKIP-PROCESS] estadoFinanciero={} motivo={}",
                item.getCuentaId(),
                t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            EstadoFinanciero item,
            Throwable t) {

        log.error(
                "[SKIP-WRITE] estadoFinanciero={} motivo={}",
                item.getCuentaId(),
                t.getMessage()
        );
    }
}
