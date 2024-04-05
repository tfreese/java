// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Diese {@link Function} liefert einen leeren String, wenn dieser aus einem der definierten Stopwörter besteht.<br>
 * Ist der String nicht in den Stop-Wörtern enthalten, wird dieser einfach zurückgegeben.<br>
 * Als Stop-Wörter werden hier (Füll)-Wörter angesehen, die keinerlei Information besitzen, z.B. wie der die das oder he she it und the.
 *
 * @author Thomas Freese
 */
public class FunctionStripStopWords implements UnaryOperator<String> {

    private static final Map<Locale, Set<String>> CACHE = new HashMap<>();

    private static FunctionStripStopWords instance;

    public static synchronized FunctionStripStopWords getInstance() {
        if (instance == null) {
            instance = new FunctionStripStopWords();
            parseStopWords();
        }

        return instance;
    }

    private static void parseStopWords() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            URL url = classLoader.getResource("stopwords_global.txt");
            final Set<String> global = new TreeSet<>(Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8));
            CACHE.put(Locale.ROOT, global);

            url = classLoader.getResource("stopwords_de.txt");
            final Set<String> de = new TreeSet<>(Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8));
            // de.addAll(global);
            CACHE.put(Locale.GERMAN, de);

            url = classLoader.getResource("stopwords_en.txt");
            final Set<String> en = new TreeSet<>(Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8));
            // en.addAll(global);
            CACHE.put(Locale.ENGLISH, en);
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String apply(final String text) {
        // @formatter:off
        if (CACHE.get(Locale.ROOT).contains(text)
            || CACHE.get(Locale.GERMAN).contains(text)
            || CACHE.get(Locale.ENGLISH).contains(text))
        {
            return "";
        }
        // @formatter:off

        return text;
    }

    /**
     * Liefert das {@link Locale} dessen Stop-Wörter am häufigsten in den Text vorkommen.<br>
     * <b>Im Mail Header 'Content-Language' ist je nach Provider das Locale schon enthalten !</b> Ist die Collection leer, wird null geliefert.<br>
     *
     * @return {@link Locale}; null oder {@link Locale#GERMAN} oder {@link Locale#ENGLISH}
     */
    public Locale guessLocale(final Collection<String> texte) {
        if (texte == null || texte.isEmpty()) {
            return null;
        }

        int german = 0;
        int english = 0;

        for (String text : texte) {
            if (CACHE.get(Locale.GERMAN).contains(text)) {
                german++;
            }

            if (CACHE.get(Locale.ENGLISH).contains(text)) {
                english++;
            }
        }

        if (german > english) {
            return Locale.GERMAN;
        }

        return Locale.ENGLISH;
    }
}
