package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author Thomas Freese
 */
public final class RhymingWordsMain {
    public static void main(final String[] args) throws IOException {
        final File file = new File("src/main/resources/stopwords_de.txt");
        System.out.println(file.getAbsolutePath());

        // do the reversing and sorting

        // write new list to standard out
        try (FileReader words = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(reverse(sort(reverse(words))))) {
            String input;

            while ((input = in.readLine()) != null) {
                System.out.println(input);
            }
        }
    }

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

    private RhymingWordsMain() {
        super();
    }
}
