// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.util.concurrent.TimeUnit;

import de.freese.sonstiges.disruptor.AbstractLoadBalancedEventHandler;

/**
 * @author Thomas Freese
 */
public class LongHandler extends AbstractLoadBalancedEventHandler<LongEvent> {
    public LongHandler() {
        super();
    }

    public LongHandler(final int parallelism, final int ordinal) {
        super(parallelism, ordinal);
    }

    @Override
    protected void doOnEvent(final LongEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        getLogger().info("{}: LongEventHandler.onEvent: Event = {}", Thread.currentThread().getName(), event);

        // Kann auch vom CleaningEventHandler erledigt werden, wenn es mehrere EventHandler sind.
        // event.clear();

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        catch (InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }
}
