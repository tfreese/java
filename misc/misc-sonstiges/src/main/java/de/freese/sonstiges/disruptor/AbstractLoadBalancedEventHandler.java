// Created: 29.09.23
package de.freese.sonstiges.disruptor;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoadBalancing for multiple {@link EventHandler} over the Sequence.<br>
 * Otherwise, all {@link EventHandler} would process the same Event.
 *
 * @author Thomas Freese
 */
public abstract class AbstractLoadBalancedEventHandler<T> implements EventHandler<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int ordinal;
    private final int parallelism;

    /**
     * Disabled LoadBalancing.
     */
    protected AbstractLoadBalancedEventHandler() {
        this(-1, -1);
    }

    protected AbstractLoadBalancedEventHandler(final int parallelism, final int ordinal) {
        super();

        this.parallelism = parallelism;
        this.ordinal = ordinal;
    }

    @Override
    public final void onEvent(final T event, final long sequence, final boolean endOfBatch) throws Exception {
        if (ordinal == -1 || ordinal == (sequence % parallelism)) {
            doOnEvent(event, sequence, endOfBatch);
        }
    }

    protected abstract void doOnEvent(T event, long sequence, boolean endOfBatch) throws Exception;

    protected Logger getLogger() {
        return logger;
    }
}
