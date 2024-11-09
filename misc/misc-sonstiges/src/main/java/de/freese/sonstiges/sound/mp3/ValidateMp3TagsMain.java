// Created: 28.09.2013
package de.freese.sonstiges.sound.mp3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import java.util.stream.Stream;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class ValidateMp3TagsMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateMp3TagsMain.class);

    public static void main(final String[] args) {
        // JUL-Logger ausschalten.
        LogManager.getLogManager().reset();

        // JUL-Logger auf slf4j umleiten.
        // SLF4JBridgeHandler.removeHandlersForRootLogger();
        // SLF4JBridgeHandler.install();

        Path rootDirectory = Paths.get("/mnt", "mediathek", "musik");
        rootDirectory = rootDirectory.resolve("Suede");

        final List<FieldKey> fields = new ArrayList<>();
        fields.add(FieldKey.ALBUM);
        fields.add(FieldKey.ALBUM_ARTIST);
        fields.add(FieldKey.ALBUM_ARTIST_SORT);
        fields.add(FieldKey.ALBUM_SORT);
        fields.add(FieldKey.ARTIST);
        fields.add(FieldKey.ARTIST_SORT);
        fields.add(FieldKey.ORIGINAL_ARTIST);
        fields.add(FieldKey.TITLE);
        fields.add(FieldKey.TITLE_SORT);

        final List<FieldKey> unwantedKeys = new ArrayList<>(Arrays.asList(FieldKey.values()));
        unwantedKeys.removeAll(fields);
        unwantedKeys.remove(FieldKey.YEAR);
        unwantedKeys.remove(FieldKey.TRACK_TOTAL);
        unwantedKeys.remove(FieldKey.TRACK);
        unwantedKeys.remove(FieldKey.RECORD_LABEL);
        unwantedKeys.remove(FieldKey.ORIGINAL_YEAR);
        // unwantedKeys.remove(FieldKey.LYRICS);
        // unwantedKeys.remove(FieldKey.LANGUAGE);
        unwantedKeys.remove(FieldKey.IS_COMPILATION);
        unwantedKeys.remove(FieldKey.DISC_TOTAL);
        unwantedKeys.remove(FieldKey.DISC_NO);
        unwantedKeys.remove(FieldKey.COVER_ART);

        final Map<File, Report> reports = new HashMap<>();

        try {
            walk(rootDirectory, audioFile -> {
                validateName(reports, audioFile, fields);

                containsText(reports, audioFile, fields, Set.of(" Feat", " Vs", " By ", " Van ", " De ", " La ", " With ", " version", " video", " remix", " dub", " mix", " cut"));

                containsCovers(reports, audioFile);

                // containsFlag(reports, audioFile, unwantedKeys);
            });
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        int i = 1;

        for (Report report : new TreeSet<>(reports.values())) {
            final String message = "%03d: %s".formatted(i++, report.toString(rootDirectory));
            LOGGER.info(message);
        }
    }
    // private static final List<FieldKey> KEYS_UNUSED = Arrays.asList(FieldKey.COMMENT, FieldKey.COMPOSER, FieldKey.ORIGINAL_ARTIST,
    // FieldKey.URL_OFFICIAL_ARTIST_SITE, FieldKey.ENCODER, FieldKey.ORIGINAL_ARTIST);

    /**
     * Prüfen ob Cover vorhanden sind.
     */
    static void containsCovers(final Map<File, Report> reports, final AudioFile audioFile) {
        final Tag tag = audioFile.getTag();
        final List<Artwork> artworks = tag.getArtworkList();

        if (artworks == null || artworks.isEmpty()) {
            return;
        }

        addReport(reports, audioFile.getFile(), "mehrere cover");

        // String value = tag.getFirst(FieldKey.COVER_ART);
        //
        // if (StringUtils.isBlank(value)) {
        // return;
        // }
        //
        // reports.add(new Report("cover", audioFile.getFile()));
    }

    /**
     * Prüfen, ob die Tags Inhalte haben.
     */
    static void containsFlag(final Map<File, Report> reports, final AudioFile audioFile, final List<FieldKey> keys) {
        final Tag tag = audioFile.getTag();

        for (FieldKey key : keys) {
            for (TagField field : tag.getFields(key)) {
                if (!(field instanceof TagTextField)) {
                    continue;
                }

                String value = null;

                try {
                    final TagTextField textField = (TagTextField) field;
                    value = textField.getContent();
                }
                catch (NullPointerException ex) {
                    // Ignore
                }

                if (value == null || value.isBlank() || FieldKey.ENCODER.equals(key) && audioFile.getFile().getName().toLowerCase().endsWith("flac")) {
                    // Bei FLAC steht immer die Bibliothek drin.
                    continue;
                }

                addReport(reports, audioFile.getFile(), key.name());
            }
        }
    }

    static void containsText(final Map<File, Report> reports, final AudioFile audioFile, final List<FieldKey> fields, final Set<String> texte) {
        final Tag tag = audioFile.getTag();

        for (FieldKey field : fields) {
            for (TagField tagField : tag.getFields(field)) {
                if (!(tagField instanceof TagTextField)) {
                    continue;
                }

                final String value = ((TagTextField) tagField).getContent();

                if (value == null || value.isEmpty()) {
                    continue;
                }

                for (String text : texte) {
                    if (value.contains(text)) {
                        addReport(reports, audioFile.getFile(), "containsText");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Prüfen die Schreibweise der Tags.
     */
    static void validateName(final Map<File, Report> reports, final AudioFile audioFile, final List<FieldKey> keys) {
        final Tag tag = audioFile.getTag();

        final String fileName = audioFile.getFile().getName();

        if (fileName.endsWith("MP3") || fileName.endsWith("WMA") || fileName.endsWith("FLAC")) {
            addReport(reports, audioFile.getFile(), "dateiname");
        }

        for (FieldKey key : keys) {
            for (TagField field : tag.getFields(key)) {
                if (!(field instanceof TagTextField textField)) {
                    continue;
                }

                String value = textField.getContent();

                if (value == null || value.isBlank()) {
                    continue;
                }

                if (value.contains("`") || value.contains("´") || value.contains("\"")) {
                    addReport(reports, audioFile.getFile(), "sonderzeichen");
                }

                if (value.startsWith(" ") || value.endsWith(" ") || value.contains("  ")) {
                    addReport(reports, audioFile.getFile(), "leerzeichen");
                }

                if (value.toLowerCase().contains(" vs ") || value.toLowerCase().contains(" feat ") || value.toLowerCase().contains(" ft ")) {
                    addReport(reports, audioFile.getFile(), "schreibweise");
                }

                // Wörter müssen mit Großbuchstaben beginnen.
                value = value.replace("\\(", "");
                value = value.replace("\\)", "");
                value = value.replace("\\.", " ");
                value = value.replace('-', ' ');

                final String[] splits = value.split(" ");

                for (String split : splits) {
                    if (split == null || split.isBlank()) {
                        continue;
                    }

                    final char c = split.charAt(0);

                    if (Character.isLetter(c) && !Character.isUpperCase(c)) {
                        addReport(reports, audioFile.getFile(), "schreibweise");
                    }
                }
            }
        }
    }

    private static void addReport(final Map<File, Report> reports, final File file, final String text) {
        final Report report = reports.computeIfAbsent(file, key -> new Report(file));

        report.addMessage(text);
    }

    private static void walk(final Path directory, final Consumer<AudioFile> consumer) throws Exception {
        try (Stream<Path> stream = Files.walk(directory)) {
            stream
                    .filter(path -> !Files.isDirectory(path))
                    .filter(p -> !p.toString().toLowerCase().endsWith(".gif"))
                    .filter(p -> !p.toString().toLowerCase().endsWith(".jpg"))
                    .filter(p -> !p.toString().toLowerCase().endsWith(".png"))
                    .filter(p -> !p.toString().toLowerCase().endsWith(".txt"))
                    // .filter(p -> !p.toString().toLowerCase().endsWith(".m4b"))
                    .sorted()
                    .forEach(path -> {
                        LOGGER.info("{}", path);

                        try {
                            final AudioFile audioFile = AudioFileIO.read(path.toFile());

                            consumer.accept(audioFile);
                        }
                        catch (RuntimeException ex) {
                            throw ex;
                        }
                        catch (Exception ex) {
                            final RuntimeException rex = new RuntimeException(ex);
                            rex.setStackTrace(ex.getStackTrace());

                            throw rex;
                        }
                    })
            ;
        }
    }

    private ValidateMp3TagsMain() {
        super();
    }
}
