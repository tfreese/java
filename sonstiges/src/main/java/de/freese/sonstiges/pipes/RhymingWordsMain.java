package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("java:S2095")
public final class RhymingWordsMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RhymingWordsMain.class);

    public static Reader reverse(final Reader source) throws IOException {
        final BufferedReader in = new BufferedReader(source);

        final PipedWriter pipeOut = new PipedWriter();
        final PipedReader pipeIn = new PipedReader(pipeOut);
        final PrintWriter out = new PrintWriter(pipeOut);

        new ReverseThread(out, in).start();

        return pipeIn;
    }

    public static Reader sort(final Reader source) throws IOException {
        final BufferedReader in = new BufferedReader(source);

        final PipedWriter pipeOut = new PipedWriter();
        final PipedReader pipeIn = new PipedReader(pipeOut);
        final PrintWriter out = new PrintWriter(pipeOut);

        new SortThread(out, in).start();

        return pipeIn;
    }

    static void main() throws IOException, URISyntaxException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("stopwords_de.txt");
        final File file = new File(url.toURI());

        // Do the reversing and sorting.

        // Write the new list to standard out.
        try (FileReader words = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(reverse(sort(reverse(words))))) {
            String input;

            while ((input = in.readLine()) != null) {
                LOGGER.info(input);
            }
        }
    }

    private RhymingWordsMain() {
        super();
    }
}
