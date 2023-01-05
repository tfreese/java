package de.freese.sonstiges.producerconsumer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public final class ProducerConsumerWaitNotify
{
    /**
     * @author Thomas Freese
     */
    private static class Consumer implements Runnable
    {
        private final CubbyHole cubbyhole;

        private final int number;

        Consumer(final CubbyHole cubbyHole, final int number)
        {
            super();

            this.cubbyhole = cubbyHole;
            this.number = number;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                int value = this.cubbyhole.get();

                System.out.printf("%s: Consumer-%d got: %d%n", Thread.currentThread().getName(), this.number, value);

                try
                {
                    TimeUnit.MILLISECONDS.sleep(3000);
                }
                catch (InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class CubbyHole
    {
        private boolean available;

        private int content;

        public synchronized int get()
        {
            while (!this.available)
            {
                try
                {
                    wait(); // wait for Producer to put value
                }
                catch (InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }

            this.available = false;
            notifyAll(); // notify Producer that value has been retrieved

            return this.content;
        }

        public synchronized void put(final int value)
        {
            while (this.available)
            {
                try
                {
                    wait(); // wait for Consumer to get value
                }
                catch (InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }

            this.content = value;
            this.available = true;

            notifyAll(); // notify Consumer that value has been set
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class Producer implements Runnable
    {
        private final CubbyHole cubbyhole;

        private final int number;

        Producer(final CubbyHole cubbyHole, final int number)
        {
            super();

            this.cubbyhole = cubbyHole;
            this.number = number;
        }

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            for (int i = 0; i < 10; i++)
            {
                this.cubbyhole.put(i);

                System.out.printf("%s: Producer-%d put: %d%n", Thread.currentThread().getName(), this.number, i);

                try
                {
                    TimeUnit.MILLISECONDS.sleep(300);
                }
                catch (InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }

            System.exit(0);
        }
    }

    public static void main(final String[] args) throws Exception
    {
        CubbyHole cubbyHole = new CubbyHole();

        Executor executor = Executors.newCachedThreadPool();

        // Producer starten
        for (int i = 0; i < 1; i++)
        {
            executor.execute(new Producer(cubbyHole, i + 1));
        }

        TimeUnit.MILLISECONDS.sleep(500);

        // Consumer starten
        for (int i = 0; i < 2; i++)
        {
            executor.execute(new Consumer(cubbyHole, i + 1));
        }

        // System.exit(0);
    }

    private ProducerConsumerWaitNotify()
    {
        super();
    }
}
