// Created: 29.09.23
package de.freese.sonstiges.disruptor;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.disruptor.example.LongEventMain;

/**
 * LoadBalancing for multiple {@link EventHandler} over the Sequence.<br>
 * Otherwise, all {@link EventHandler} would process the same Event.
 *
 * @author Thomas Freese
 */
public abstract class AbstractLoadBalancedEventHandler<T> implements EventHandler<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int ordinal;

    protected AbstractLoadBalancedEventHandler() {
        this(-1); // Disable LoadBalancing
    }

    protected AbstractLoadBalancedEventHandler(final int ordinal) {
        super();

        this.ordinal = ordinal;
    }

    @Override
    public final void onEvent(final T event, final long sequence, final boolean endOfBatch) throws Exception {
        if ((this.ordinal == -1) || (this.ordinal == (sequence % LongEventMain.THREAD_COUNT))) {
            doOnEvent(event, sequence, endOfBatch);
        }
    }

    protected abstract void doOnEvent(final T event, final long sequence, final boolean endOfBatch) throws Exception;

    protected Logger getLogger() {
        return logger;
    }
}
