package de.freese.sonstiges.producerconsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class ProducerConsumerBlockingQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConsumerBlockingQueue.class);

    /**
     * @author Thomas Freese
     */
    private static final class Consumer implements Runnable {
        private final int number;
        private final BlockingQueue<Integer> queue;

        Consumer(final BlockingQueue<Integer> queue, final int number) {
            super();

            this.queue = queue;
            this.number = number;
        }

        @Override
        public synchronized void run() {
            while (!Thread.interrupted()) {
                try {
                    final Integer value;
                    // value = queue.take();
                    value = queue.poll(5000, TimeUnit.MILLISECONDS);

                    if (value == null) {
                        break;
                    }

                    LOGGER.info("{}: Consumer-{} got: {}", Thread.currentThread().getName(), number, value);

                    TimeUnit.MILLISECONDS.sleep(3000);
                }
                catch (InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);

                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class Producer implements Runnable {
        private final int number;
        private final BlockingQueue<Integer> queue;

        Producer(final BlockingQueue<Integer> queue, final int number) {
            super();

            this.queue = queue;
            this.number = number;
        }

        @Override
        public synchronized void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.put(i);
                    // queue.offer(Integer.valueOf(i)); // Doesn't work with SynchronousQueue.

                    LOGGER.info("{}: Producer-{} put: {}", Thread.currentThread().getName(), number, i);

                    TimeUnit.MILLISECONDS.sleep(300);
                }
                catch (InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);

                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static void main() throws Exception {
        // final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        // final BlockingQueue<Integer> queue = new SynchronousQueue<>();

        try (ExecutorService executorService = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 1; i++) {
                executorService.execute(new Producer(queue, i + 1));
            }

            TimeUnit.MILLISECONDS.sleep(500);

            for (int i = 0; i < 2; i++) {
                executorService.execute(new Consumer(queue, i + 1));
            }
        }
    }

    private ProducerConsumerBlockingQueue() {
        super();
    }
}
