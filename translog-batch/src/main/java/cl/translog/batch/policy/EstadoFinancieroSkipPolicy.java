package cl.translog.batch.policy;

import cl.translog.batch.exception.InvalidEstadoFinancieroException;

import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;


@Component
public class EstadoFinancieroSkipPolicy implements SkipPolicy {

    private static final Logger log =
            LoggerFactory.getLogger(EstadoFinancieroSkipPolicy.class);

    private static final long MAX_SKIPS = 200;

    @Override
    public boolean shouldSkip(
            Throwable throwable,
            long skipCount) {

        boolean skippable =
                throwable instanceof InvalidEstadoFinancieroException
                        || throwable instanceof FlatFileParseException
                        || throwable instanceof DateTimeParseException
                        || throwable instanceof IllegalArgumentException;

        boolean allowed =
                skippable && skipCount < MAX_SKIPS;

        log.warn(
                "SkipPolicy -> exception={}, skipCount={}, allowed={}",
                throwable.getClass().getSimpleName(),
                skipCount,
                allowed
        );

        return allowed;
    }
}
