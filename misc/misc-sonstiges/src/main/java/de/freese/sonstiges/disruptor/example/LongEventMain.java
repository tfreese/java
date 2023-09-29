// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * <a href="https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started">Getting-Started</a>.
 *
 * @author Thomas Freese
 */
public final class LongEventMain {
    /**
     * -2 damit noch Platz für den CleaningEventHandler und sonstige Ressourcen bleibt.
     */
    public static final int THREAD_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);

    public static void main(final String[] args) throws Exception {
        // Specify the size of the ring buffer, must be power of 2.
        // int ringBufferSize = Integer.highestOneBit(31) << 1;
        int ringBufferSize = 32;

        // Threads werden vom Distributor exklusiv belegt und erst beim Shutdown wieder freigegeben.
        // Daher ist ein Executor nicht empfohlen, jeder Disruptor braucht seinen eigenen exklusiven ThreadPool.
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        // ThreadFactory threadFactory = new CustomizableThreadFactory("disruptor-thread-");
        // ThreadFactory threadFactory = new
        // BasicThreadFactory.Builder().namingPattern("disruptor-thread-%d").daemon(true).priority(Thread.NORM_PRIORITY).build();

        ProducerType producerType = ProducerType.SINGLE; // Nur ein exklusiver Thread schreibt Daten in den RingBuffer.
        // ProducerType producerType = ProducerType.MULTI; // Verschiedene Threads schreiben Daten in den RingBuffer.

        WaitStrategy waitStrategy = null;

        // The BlockingWaitStrategy is the slowest of the available wait strategies, but is the most conservative with the respect
        // to CPU usage and will give the most consistent behaviour across the widest variety of deployment options.
        // waitStrategy = new BlockingWaitStrategy();

        // It works best in situations where low latency is not required, but a low impact on the producing thread is desired.
        // A common use case is for asynchronous logging.
        // waitStrategy = new SleepingWaitStrategy();

        // This is the recommended wait strategy when need very high performance and the number of Event Handler threads is
        // less than the total number of logical cores, e.g. you have hyper-threading enabled.
        // waitStrategy = new YieldingWaitStrategy();

        // This wait strategy should only be used if the number of Event Handler threads is smaller than the number of physical cores on the box.
        waitStrategy = new BusySpinWaitStrategy();

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        // Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, ringBufferSize, executorService, producerType, waitStrategy);
        //Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, ringBufferSize, threadFactory, producerType, waitStrategy);
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, ringBufferSize, threadFactory);

        // EventHandler verarbeiten alle parallel ein Event -> LoadBalancing notwendig falls nur ein EventHandler arbeiten soll.
        // Connect a single handler
        //        disruptor.handleEventsWith(new LongHandler()).then(new CleaningEventHandler());

        // Connect multiple Handlers with load balancing
        EventHandler<LongEvent>[] handlers = new LongHandler[THREAD_COUNT];

        for (int ordinal = 0; ordinal < handlers.length; ordinal++) {
            handlers[ordinal] = new LongHandler(ordinal);
        }

        disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());
        //        // disruptor.setDefaultExceptionHandler(...);

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //LongEventProducer producer = new LongEventProducer(ringBuffer);
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);

        for (long l = 0; l < 50; l++) {
            bb.putLong(0, l);
            producer.onData(bb); // Wartet, wenn der RingBuffer voll ist, ggf. ringBufferSize anpassen
        }

        TimeUnit.MILLISECONDS.sleep(2000);

        // Nur notwendig, wenn die Event-Publizierung noch nicht abgeschlossen ist.
        disruptor.halt();
        disruptor.shutdown(5, TimeUnit.SECONDS);

        executorService.shutdown();
    }

    private LongEventMain() {
        super();
    }
}
