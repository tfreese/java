package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Thomas Freese
 */
public class SortThread extends Thread
{
    /**
     *
     */
    private static final int MAXWORDS = 50;

    /**
     * @param a String[]
     * @param lo0 int
     * @param hi0 int
     */
    private static void quicksort(final String[] a, final int lo0, final int hi0)
    {
        int lo = lo0;
        int hi = hi0;

        if (lo >= hi)
        {
            return;
        }

        String mid = a[(lo + hi) / 2];

        while (lo < hi)
        {
            while ((lo < hi) && (a[lo].compareTo(mid) < 0))
            {
                lo++;
            }

            while ((lo < hi) && (a[hi].compareTo(mid) > 0))
            {
                hi--;
            }

            if (lo < hi)
            {
                String value = a[lo];
                a[lo] = a[hi];
                a[hi] = value;
                lo++;
                hi--;
            }
        }

        if (hi < lo)
        {
            int t = hi;
            hi = lo;
            lo = t;
        }

        quicksort(a, lo0, lo);
        quicksort(a, (lo == lo0) ? (lo + 1) : lo, hi0);
    }

    /**
     *
     */
    private final BufferedReader in;
    /**
     *
     */
    private final PrintWriter out;

    /**
     * Creates a new {@link SortThread} object.
     *
     * @param out {@link PrintWriter}
     * @param in {@link BufferedReader}
     */
    public SortThread(final PrintWriter out, final BufferedReader in)
    {
        super();

        this.out = out;
        this.in = in;
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        if ((this.out != null) && (this.in != null))
        {
            try
            {
                String[] listOfWords = new String[MAXWORDS];
                int numWords = 0;

                while ((listOfWords[numWords] = this.in.readLine()) != null)
                {
                    numWords++;

                    if (numWords == (MAXWORDS - 1))
                    {
                        break;
                    }
                }

                quicksort(listOfWords, 0, numWords - 1);

                for (int i = 0; i < numWords; i++)
                {
                    this.out.println(listOfWords[i]);
                }

                this.out.close();
            }
            catch (IOException ex)
            {
                System.err.println("SortThread run: " + ex);
            }
        }
    }
}
