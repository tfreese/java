// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * <a href="https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started">Getting-Started</a>.
 *
 * @author Thomas Freese
 */
public final class LongEventMain {
    /**
     * -2 to keep Space for CleaningEventHandler and other Resources.
     */
    public static final int THREAD_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);

    static void main() throws Exception {
        // Specify the size of the ring buffer, must be power of 2.
        // int ringBufferSize = Integer.highestOneBit(31) << 1;
        final int ringBufferSize = 32;

        // Threads are allocated exclusive from Distributor and released on Shutdown.
        // Every Disruptor needs a dedicated ThreadPool.
        final ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        // final ThreadFactory threadFactory = new CustomizableThreadFactory("disruptor-thread-");
        // final ThreadFactory threadFactory = new
        // BasicThreadFactory.Builder().namingPattern("disruptor-thread-%d").daemon(true).priority(Thread.NORM_PRIORITY).build();

        // ProducerType producerType = ProducerType.SINGLE; // Only one Thread is writing into the RingBuffer.
        // final ProducerType producerType = ProducerType.MULTI; // Multiple Threads are written into the RingBuffer.

        // final WaitStrategy waitStrategy = null;

        // The BlockingWaitStrategy is the slowest of the available wait strategies, but is the most conservative with the respect
        // to CPU usage and will give the most consistent behaviour across the widest variety of deployment options.
        // waitStrategy = new BlockingWaitStrategy();

        // It works best in situations where low latency is not required, but a low impact on the producing thread is desired.
        // A common use case is for asynchronous logging.
        // waitStrategy = new SleepingWaitStrategy();

        // This is the recommended wait strategy when need very high performance, and the number of Event Handler threads is
        // lower than the total number of logical cores, e.g., you have hyper-threading enabled.
        // waitStrategy = new YieldingWaitStrategy();

        // This wait strategy should only be used if the number of Event Handler threads is smaller than the number of physical cores on the box.
        // waitStrategy = new BusySpinWaitStrategy();

        // try (ExecutorService executorService = Executors.newFixedThreadPool(8)) {
        // final Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, ringBufferSize, executorService, producerType, waitStrategy);
        // final Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, ringBufferSize, threadFactory, producerType, waitStrategy);
        final Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, ringBufferSize, threadFactory);

        // EventHandler processing an Event in parallel -> LoadBalancing required if only one EventHandler should do this.
        // Connect a single handler.
        // disruptor.handleEventsWith(new LongHandler()).then(new CleaningEventHandler());

        // Connect multiple Handlers with load balancing.
        final EventHandler<LongEvent>[] handlers = new LongHandler[THREAD_COUNT];

        for (int ordinal = 0; ordinal < handlers.length; ordinal++) {
            handlers[ordinal] = new LongHandler(THREAD_COUNT, ordinal);
        }

        disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());
        // disruptor.setDefaultExceptionHandler(...);

        // Start the Disruptor, starts all threads running.
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        final RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        // final LongEventProducer producer = new LongEventProducer(ringBuffer);
        final LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);

        final ByteBuffer bb = ByteBuffer.allocate(8);

        for (long l = 0; l < 50; l++) {
            bb.putLong(0, l);
            producer.onData(bb); // Waits, if the RingBuffer is full, e.g., adjust the ringBufferSize.
        }

        TimeUnit.MILLISECONDS.sleep(2000);

        // Only necessary, if the Event-Publishing is not finished.
        disruptor.halt();
        disruptor.shutdown(5, TimeUnit.SECONDS);
        // }
    }

    private LongEventMain() {
        super();
    }
}
