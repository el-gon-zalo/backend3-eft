package cl.translog.batch.policy;

import cl.translog.batch.exception.InvalidInteresException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
public class InteresSkipPolicy implements SkipPolicy {

    private static final Logger log =
            LoggerFactory.getLogger(InteresSkipPolicy.class);

    private static final long MAX_SKIPS = 200;

    @Override
    public boolean shouldSkip(
            Throwable throwable,
            long skipCount) {

        boolean skippable =
                throwable instanceof InvalidInteresException
                        || throwable instanceof FlatFileParseException;

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
