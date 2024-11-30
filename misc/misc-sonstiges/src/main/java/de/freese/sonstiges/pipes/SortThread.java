package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class SortThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(SortThread.class);

    private static final int MAX_WORDS = 50;

    private static void quicksort(final String[] a, final int lo0, final int hi0) {
        int lo = lo0;
        int hi = hi0;

        if (lo >= hi) {
            return;
        }

        final String mid = a[(lo + hi) / 2];

        while (lo < hi) {
            while (lo < hi && a[lo].compareTo(mid) < 0) {
                lo++;
            }

            while (lo < hi && a[hi].compareTo(mid) > 0) {
                hi--;
            }

            if (lo < hi) {
                final String value = a[lo];
                a[lo] = a[hi];
                a[hi] = value;
                lo++;
                hi--;
            }
        }

        if (hi < lo) {
            // final int t = hi;
            // hi = lo;
            // lo = t;
            lo = hi;
        }

        quicksort(a, lo0, lo);
        quicksort(a, (lo == lo0) ? (lo + 1) : lo, hi0);
    }

    private final BufferedReader in;
    private final PrintWriter out;

    public SortThread(final PrintWriter out, final BufferedReader in) {
        super();

        this.out = out;
        this.in = in;
    }

    @Override
    public void run() {
        if (this.out != null && this.in != null) {
            try {
                final String[] listOfWords = new String[MAX_WORDS];
                int numWords = 0;

                while ((listOfWords[numWords] = this.in.readLine()) != null) {
                    numWords++;

                    if (numWords == (MAX_WORDS - 1)) {
                        break;
                    }
                }

                quicksort(listOfWords, 0, numWords - 1);

                for (int i = 0; i < numWords; i++) {
                    this.out.println(listOfWords[i]);
                }

                this.out.close();
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
