// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class CleaningEventHandler implements EventHandler<LongEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleaningEventHandler.class);

    @Override
    public void onEvent(final LongEvent event, final long sequence, final boolean endOfBatch) {
        LOGGER.info("{}: CleaningEventHandler.onEvent: Sequence {}", Thread.currentThread().getName(), sequence);

        event.clear();
    }
}
