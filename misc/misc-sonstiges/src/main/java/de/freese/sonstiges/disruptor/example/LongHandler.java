// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class LongHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongHandler.class);

    private final int ordinal;

    public LongHandler() {
        this(-1);
    }

    public LongHandler(final int ordinal) {
        super();

        this.ordinal = ordinal;
    }

    @Override
    public void onEvent(final LongEvent event, final long sequence, final boolean endOfBatch) {
        // Load-Balancing auf die Handler über die Sequence.
        // Sonst würden alle Handler gleichzeitig eine Sequence bearbeiten.
        if ((this.ordinal == -1) || (this.ordinal == (sequence % LongEventMain.THREAD_COUNT))) {
            handleEvent(event);
        }
    }

    @Override
    public void onEvent(final LongEvent event) throws Exception {
        handleEvent(event);
    }

    private void handleEvent(final LongEvent event) {
        LOGGER.info("{}: LongEventHandler.onEvent: Event = {}", Thread.currentThread().getName(), event);

        // Kann auch vom CleaningEventHandler erledigt werden, wenn es mehrere EventHandler sind.
        // event.clear();

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
