package net.led.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public class YahooProvider implements Runnable {
    private static Double parseDouble(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }

        if (s.endsWith("%")) {
            s = s.substring(0, s.length() - 1);
        }

        try {
            return Double.valueOf(s);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

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
        Thread currentThread = Thread.currentThread();

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
                    // Empty
                }
            }
        }
    }

    public void start() {
        this.feedThread = new Thread(this, "Yahoo Provider");
        this.feedThread.start();
    }

    public void stop() {
        Thread t = this.feedThread;
        this.feedThread = null;

        if (t != null) {
            t.interrupt();
        }
    }

    /**
     * Reads data from Yahoo! for each symbol.
     */
    private void readSymbolData(final String symbol) {
        String feedURL = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sl9p4&e=.csv";
        URL url = null;

        try {
            url = URI.create(feedURL).toURL();
        }
        catch (MalformedURLException ex) {
            System.err.println("Unable to open connection !");
            return;
        }

        String line = null;
        StringTokenizer st;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            line = br.readLine();
        }
        catch (IOException ex) {
            // Empty
        }

        if (line != null) {
            st = new StringTokenizer(line, ",");

            if (st.hasMoreTokens()) {
                String name = st.nextToken();

                if (name.startsWith("\"")) {
                    name = name.substring(1, name.length() - 1);
                }

                if (!(name.equals(symbol))) {
                    return;
                }
            }
            else {
                return;
            }

            Double last = null;

            if (st.hasMoreTokens()) {
                last = parseDouble(st.nextToken());
            }

            Double changePercent = null;

            if (st.hasMoreTokens()) {
                changePercent = parseDouble(st.nextToken());

                // Sometimes the feed sends invalid data (like -9999.00)
                // for change percent
                if (Math.abs(changePercent) > 50D) {
                    changePercent = Double.NaN;
                }
            }

            if ((last != null) && (changePercent != null)) {
                Stock stock = new Stock(symbol, last, changePercent);
                sendStock(stock);
            }
        }
    }

    private void sendStock(final Stock stock) {
        synchronized (this.listeners) {
            for (UpdateListener element : this.listeners) {
                element.update(stock);
            }
        }
    }
}
