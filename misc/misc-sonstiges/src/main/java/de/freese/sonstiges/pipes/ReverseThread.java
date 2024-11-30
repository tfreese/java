package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class ReverseThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseThread.class);

    private static String reverseIt(final String source) {
        int i = 0;
        final int len = source.length();
        final StringBuilder sb = new StringBuilder(len);

        for (i = len - 1; i >= 0; i--) {
            sb.append(source.charAt(i));
        }

        return sb.toString();
    }

    private final BufferedReader in;
    private final PrintWriter out;

    public ReverseThread(final PrintWriter out, final BufferedReader in) {
        super();

        this.out = out;
        this.in = in;
    }

    @Override
    public void run() {
        if (this.out != null && this.in != null) {
            try {
                String input = null;

                while ((input = this.in.readLine()) != null) {
                    this.out.println(reverseIt(input));
                    this.out.flush();
                }

                this.out.close();
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
