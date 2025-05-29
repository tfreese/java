// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.util.Locale;
import java.util.function.UnaryOperator;

import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.GermanStemmer;

/**
 * @author Thomas Freese
 */
public class FunctionStemmer implements UnaryOperator<String> {
    /**
     * German Stemmer
     */
    public static final UnaryOperator<String> DE = new FunctionStemmer(Locale.GERMAN);

    /**
     * Englisch Stemmer
     */
    public static final UnaryOperator<String> EN = new FunctionStemmer(Locale.ENGLISH);

    /**
     * @author Thomas Freese
     */
    private interface Stemmer extends UnaryOperator<String> {
        @Override
        default String apply(final String s) {
            return stem(s);
        }

        String stem(String token);
    }

    // /**
    //  * @author Thomas Freese
    //  */
    // static class LuceneEnglishMinimalStemmer implements Stemmer {
    //     /**
    //      * org.apache.lucene.analysis.en.PorterStemmer
    //      */
    //     private final EnglishMinimalStemmer impl = new EnglishMinimalStemmer();
    //
    //     @Override
    //     public String stem(final String token) {
    //         final char[] ca = token.toCharArray();
    //         final int length = impl.stem(ca, ca.length);
    //
    //         return new String(ca, 0, length);
    //     }
    // }

    // /**
    //  * @author Thomas Freese
    //  */
    // static class LuceneGermanLightStemmer implements Stemmer {
    //     private final GermanLightStemmer impl = new GermanLightStemmer();
    //
    //     @Override
    //     public String stem(final String token) {
    //         final char[] ca = token.toCharArray();
    //         final int length = impl.stem(ca, ca.length);
    //
    //         return new String(ca, 0, length);
    //     }
    // }

    /**
     * @author Thomas Freese
     */
    static class SnowballEnglishStemmer implements Stemmer {
        private final EnglishStemmer impl = new EnglishStemmer();

        @Override
        public String stem(final String token) {
            impl.setCurrent(token);
            impl.stem();

            return impl.getCurrent();
        }
    }

    /**
     * @author Thomas Freese
     */
    static class SnowballGermanStemmer implements Stemmer {
        private final GermanStemmer impl = new GermanStemmer();

        @Override
        public String stem(final String token) {
            impl.setCurrent(token);
            impl.stem();

            return impl.getCurrent();
        }
    }

    public static UnaryOperator<String> get(final Locale locale) {
        if (Locale.GERMAN.equals(locale)) {
            return DE;
        }
        else if (Locale.ENGLISH.equals(locale)) {
            return EN;
        }
        else {
            throw new IllegalArgumentException("not supported locale: " + locale);
        }
    }

    private final Stemmer stemmer;

    public FunctionStemmer(final Locale locale) {
        super();

        if (Locale.GERMAN.equals(locale)) {
            stemmer = new SnowballGermanStemmer();
            // stemmer = new LuceneGermanLightStemmer();
        }
        else if (Locale.ENGLISH.equals(locale)) {
            stemmer = new SnowballEnglishStemmer();
            // stemmer = new LuceneEnglishMinimalStemmer();
        }
        else {
            throw new IllegalArgumentException("not supported locale: " + locale);
        }
    }

    @Override
    public String apply(final String text) {
        return stemmer.stem(text);
    }
}
