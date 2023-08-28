// Created: 28.08.23
package de.freese.sonstiges.imap;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.imap.analyze.FunctionNormalizeGerman;
import de.freese.sonstiges.imap.analyze.FunctionStemmer;
import de.freese.sonstiges.imap.analyze.FunctionStripNotLetter;
import de.freese.sonstiges.imap.analyze.FunctionStripSameChar;
import de.freese.sonstiges.imap.analyze.FunctionStripStopWords;
import de.freese.sonstiges.imap.model.MessageWrapper;
import de.freese.sonstiges.imap.textpart.AbstractTextPart;

/**
 * @author Thomas Freese
 */
public class TokenFunction implements Function<MessageWrapper, Map<String, Integer>> {

    public static final UnaryOperator<List<String>> PRE_FILTER = token -> {
        // String linkRegEx = "^((http[s]?|ftp|file):\\/)?\\/?([^:\\/\\s]+)(:([^\\/]*))?((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(\\?([^#]*))?(#(.*))?$";
        String linkRegEx = "^((http[s]?|ftp|file):.*)|(^(www.).*)";
        String mailRegEx = "^(.+)@(.+).(.+)$"; // ^[A-Za-z0-9+_.-]+@(.+)$

        // @formatter:off
        return token.stream()
                .map(t -> t.replace("\n", " ").replace("\r", " ")) // Remove LineBreaks
                // .peek(System.out::println)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                .parallel()
                .filter(Objects::nonNull)
                .filter(t -> !t.isBlank())
                .map(String::strip)
                .map(String::toLowerCase)
                .filter(t -> !t.matches(linkRegEx)) // Remove URLs
                .filter(t -> !t.matches(mailRegEx)) // Remove Mails
                // .filter(t -> !t.startsWith("http:"))
                // .filter(t -> !t.startsWith("https:"))
                // .filter(t -> !t.startsWith("ftp:"))
                // .filter(t -> !t.startsWith("file:"))
                // .filter(t -> !t.contains("@"))
                .map(FunctionStripNotLetter.INSTANCE)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(t -> !t.isBlank())
                .map(String::strip)
                .map(FunctionStripSameChar.INSTANCE)
                .filter(t -> t.length() > 2)
                .sorted()
                .toList()
                ;
        // @formatter:on
    };

    public static final BiFunction<List<String>, Locale, Map<String, Integer>> STEMMER_FILTER = (token, locale) -> {
        Function<String, String> functionStemmer = FunctionStemmer.get(locale);

        // @formatter:off
        return token.stream()
                .map(t -> Locale.GERMAN.equals(locale) ? FunctionNormalizeGerman.INSTANCE.apply(t) : t)
                .map(FunctionStripStopWords.getInstance())
                .map(functionStemmer).filter(t -> t.length() > 2)
                .sorted()
                // .peek(System.out::println)
                // .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting())); // long
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.summingInt(e -> 1)))
                ;
        // @formatter:on
    };
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenFunction.class);

    @Override
    public Map<String, Integer> apply(final MessageWrapper message) {
        if (message == null) {
            return null;
        }

        try {
            LOGGER.info("message: {} - {} - {}", message.getDate(), message.getSubject(), message.getFrom());

            List<AbstractTextPart> textParts = message.getTextParts();

            if ((textParts == null) || textParts.isEmpty()) {
                LOGGER.warn("no text for: {} - {} - {}", message.getDate(), message.getSubject(), message.getFrom());
                return null;
            }

            // @formatter:off
            List<String> token = textParts.stream()
                    .map(AbstractTextPart::getText)
                    .map(t -> Jsoup.parse(t).text()) // HTML-Text extrahieren
                    .map(t -> t.split(" "))
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(t -> !t.isBlank())
                    // peek(System.out::println)
                    .toList()
                    ;
            // @formatter:on

            if (token.isEmpty()) {
                LOGGER.warn("no token for: {} - {} - {}", message.getDate(), message.getSubject(), message.getFrom());
                return null;
            }

            Locale locale = FunctionStripStopWords.getInstance().guessLocale(token);

            token = PRE_FILTER.apply(token);

            return STEMMER_FILTER.apply(token, locale);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }
}
