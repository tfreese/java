// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import static de.freese.dependency.utils.Utils.PATTERN_DOUBLE_DOT;
import static de.freese.dependency.utils.Utils.PATTERN_EQUAL;
import static de.freese.dependency.utils.Utils.PATTERN_SPACE;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.utils.Utils;

/**
 * @author Thomas Freese
 */
final class CoordinateSupplierGradleProperties implements CoordinateSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinateSupplierGradleProperties.class);

    /**
     * ^#G:A [a-zA-Z0-9.\\-]*:[a-zA-Z0-9.\\-]*$
     * ^#G:A [a-zA-Z\\d.\\-]*:[a-zA-Z\\d.\\-]*$
     */
    private static final Pattern PATTERN_GA = Pattern.compile("^#G:A [\\w.\\-]*:[\\w.\\-]*$", Pattern.UNICODE_CHARACTER_CLASS);

    private final Path path;

    CoordinateSupplierGradleProperties(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public List<Coordinate> get() {
        final Predicate<String> predicateGa = value -> value.startsWith("#G:A");
        final Predicate<String> predicateVersion = value -> value.contains("version");

        final List<String> lines;

        // Files.readAllLines(path)
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            lines = stream
                    .filter(Objects::nonNull)
                    .filter(line -> !line.isBlank())
                    .filter(predicateGa.or(predicateVersion))
                    .toList();
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }

        final List<Coordinate> coordinates = new ArrayList<>();

        for (final Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            final String gaLine = iterator.next();

            final Matcher matcher = PATTERN_GA.matcher(gaLine);

            if (matcher.matches()) {
                final String versionLine = iterator.next();

                if (versionLine.startsWith("#")) {
                    continue;
                }

                final String[] coordinateSplits = PATTERN_DOUBLE_DOT.split(PATTERN_SPACE.split(gaLine)[1]);
                final String groupID = coordinateSplits[0].strip();
                final String artifactID = coordinateSplits[1].strip();
                final String version = PATTERN_EQUAL.split(versionLine)[1].strip();

                coordinates.add(new Coordinate(groupID, artifactID, version, Utils.toSource(path)));
            } else if (predicateGa.test(gaLine)) {
                LOGGER.warn("invalid pattern: {}", gaLine);
            }
        }

        return coordinates;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoordinateSupplierGradleProperties.class.getSimpleName() + "[", "]")
                .add("path=" + path)
                .toString();
    }
}
