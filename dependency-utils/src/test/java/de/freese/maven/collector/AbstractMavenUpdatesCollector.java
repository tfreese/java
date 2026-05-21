// Created: 28.12.2015
package de.freese.maven.collector;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.update.coordinate.Coordinate;

/**
 * <pre>{@code
 * Supplier: Erzeugt das Zielobjekt (List<Koordinate>)
 * Accumulator: Nimmt Elemente (Coordinate) aus dem Stream und setzt es in das Zielobjekt (List<Coordinate>)
 * Combiner: Zusammenfassen zweier Zielobjekte, wird nur bei parallelen Streams benötigt
 * Finisher: Liefert das Endergebnis (Stream<Coordinate>) aus dem Zielobjekt (List<Coordinate>)
 * }</pre>
 *
 * @author Thomas Freese
 */
public abstract class AbstractMavenUpdatesCollector {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected void doAccumulate(final List<Coordinate> updates, final String s) {
        // Neue Koordinaten auslesen und in updates einfügen.
    }

    protected List<Coordinate> doCombine(final List<Coordinate> updatesLeft, final List<Coordinate> updatesRight) {
        throw new RuntimeException("Collector does not support parallelism");

        // final List<Koordinate> list = new ArrayList<>();
        // list.addAll(updatesLeft);
        // list.addAll(updatesRight);
        //
        // return list;
    }

    protected Stream<Coordinate> doFinish(final List<Coordinate> updates) {
        if (updates == null || updates.isEmpty()) {
            return Stream.empty();
        }

        return updates.stream();
    }

    protected Logger getLogger() {
        return logger;
    }
}
