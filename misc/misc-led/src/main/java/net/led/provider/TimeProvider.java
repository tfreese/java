package net.led.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public class TimeProvider implements Runnable {
    private final List<UpdateListener> listeners = new ArrayList<>();
    private final List<String> symbols = new ArrayList<>();

    private Thread feedThread;

    public void addSymbol(final String symbol) {
        synchronized (this.symbols) {
            this.symbols.add(symbol);
        }
    }

    public void addUpdateListener(final UpdateListener listener) {
        synchronized (this.listeners) {
            if (!this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }

    public void removeAllElements() {
        synchronized (this.symbols) {
            this.symbols.clear();
        }
    }

    public void removeSymbol(final String symbol) {
        synchronized (this.symbols) {
            this.symbols.remove(symbol);
        }
    }

    public void removeUpdateListener(final UpdateListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }

    @Override
    public void run() {
        final Thread currentThread = Thread.currentThread();

        int index = 0;
        String symbol;
        long time;

        while (currentThread == this.feedThread) {
            time = System.currentTimeMillis();

            synchronized (this.symbols) {
                if (index >= this.symbols.size()) {
                    index = 0;
                }

                if (this.symbols.isEmpty()) {
                    symbol = null;
                }
                else {
                    symbol = this.symbols.get(index++);
                }
            }

            if (symbol != null) {
                readSymbolData(symbol);
            }

            time = 1000 - (System.currentTimeMillis() - time);

            if (time > 10) {
                try {
                    TimeUnit.MILLISECONDS.sleep(time);
                }
                catch (InterruptedException ex) {
                    // Ignore
                }
            }
        }
    }

    public void start() {
        this.feedThread = new Thread(this, "Time Provider");
        this.feedThread.start();
    }

    public void stop() {
        final Thread t = this.feedThread;
        this.feedThread = null;

        if (t != null) {
            t.interrupt();
        }
    }

    /**
     * Reads data from Yahoo! for each symbol.
     */
    private void readSymbolData(final String symbol) {
        sendTime(new Date());
    }

    private void sendTime(final Date newValue) {
        synchronized (this.listeners) {
            for (UpdateListener element : this.listeners) {
                element.update(newValue);
            }
        }
    }
}
