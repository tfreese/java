package de.freese.sonstiges.imap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.sonstiges.imap.analyze.FunctionStemmer;
import de.freese.sonstiges.imap.analyze.FunctionStripNotLetter;
import de.freese.sonstiges.imap.analyze.FunctionStripStopWords;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestMail {

    private static PrintStream printStream = System.out;
    private static String textHtml1;
    private static String textHtml2;
    private static String textPlain;

    @BeforeAll
    public static void beforeAll() throws Exception {
        if (!Boolean.parseBoolean(System.getProperty("run_in_ide", "false"))) {
            printStream = new PrintStream(OutputStream.nullOutputStream(), false);
        }

        final Charset charset = StandardCharsets.UTF_8;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("mail1.txt"), charset))) {
            textPlain = bufferedReader.lines().collect(Collectors.joining(" "));
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("mail1.html"), charset))) {
            textHtml1 = bufferedReader.lines().collect(Collectors.joining(" "));
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("mail2.html"), charset))) {
            textHtml2 = bufferedReader.lines().collect(Collectors.joining(" "));
        }
    }

    @AfterEach
    public void afterEach() {
        // Empty
    }

    @BeforeEach
    public void beforeEach() {
        // Empty
    }

    @Test
    void testFunctionStemmer() throws Exception {
        String ref = "wald";
        assertEquals(ref, FunctionStemmer.DE.apply(ref));
        assertEquals(ref, FunctionStemmer.DE.apply("wälder"));

        ref = "trademark";
        assertEquals(ref, FunctionStemmer.EN.apply(ref));
        assertEquals(ref, FunctionStemmer.EN.apply("trademarks"));
    }

    @Test
    void testFunctionStripNotLetter() throws Exception {
        String text = "abcdefghijklmnopqrstuvwxyz";
        assertEquals(text, FunctionStripNotLetter.INSTANCE.apply(text));

        text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertEquals(text, FunctionStripNotLetter.INSTANCE.apply(text));

        text = " aBc ";
        assertEquals(" aBc ", FunctionStripNotLetter.INSTANCE.apply(text));

        text = ",.-#+´aBc!\"§$%&/()=";
        assertEquals("      aBc          ", FunctionStripNotLetter.INSTANCE.apply(text));

        text = "0123aBc6789";
        assertEquals("    aBc    ", FunctionStripNotLetter.INSTANCE.apply(text));
    }

    @Test
    void testHtml1ToText() throws Exception {
        final String text = Jsoup.parse(textHtml1).text();
        printStream.println(text);

        prepare(text);

        assertTrue(true);
    }

    @Test
    void testHtml2ToText() throws Exception {
        final String text = Jsoup.parse(textHtml2).text();
        printStream.println(text);

        prepare(text);

        assertTrue(true);
    }

    @Test
    void testTextPlain() throws Exception {
        // String text = new Html2Text().parse(TEXT_PLAIN).getText();
        final String text = textPlain;
        printStream.println(text);

        prepare(text);

        assertTrue(true);
    }

    /**
     * Verarbeitet den Text für die Verwendung als Spamfilter.
     */
    private void prepare(final String text) {
        printStream.println();
        printStream.println("========================================================================================================");

        // @formatter:off
        List<String> token = Stream.of(text)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                // peek(System.out::println)
                .toList()
                ;
        // @formatter:on

        final Locale locale = FunctionStripStopWords.getInstance().guessLocale(token);

        token = TokenFunction.PRE_FILTER.apply(token);
        token.forEach(printStream::println);

        printStream.println();
        printStream.println("Stemmer --------------------");
        final Map<String, Integer> wordCount = TokenFunction.STEMMER_FILTER.apply(token, locale);
        wordCount.forEach((word, count) -> printStream.printf("%s - %d%n", word, count));
    }
}
