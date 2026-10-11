package cl.translog.batch.listener;

import cl.translog.batch.domain.Interes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class InteresSkipListener
        implements SkipListener<Interes, Interes> {

    private static final Logger log =
            LoggerFactory.getLogger(InteresSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(
                "[SKIP-READ] {}",
                t.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            Interes item,
            Throwable t) {

        log.error(
                "[SKIP-PROCESS] interes={} motivo={}",
                item.getCuentaId(),
                t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            Interes item,
            Throwable t) {

        log.error(
                "[SKIP-WRITE] interes={} motivo={}",
                item.getCuentaId(),
                t.getMessage()
        );
    }
}
