// Created: 28.12.2015
package de.freese.maven.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.freese.dependency.update.coordinate.Coordinate;

/**
 * Klasse eines eigenen Collectors für Maven-UpdateResolver der Java-Streaming-API.<br>
 * readerIn.lines()<br>
 * .collect(Collector.of(MavenUpdatesCollectorSimple::new<br>
 * , MavenUpdatesCollectorSimple::accumulate<br>
 * , MavenUpdatesCollectorSimple::combine<br>
 * , MavenUpdatesCollectorSimple::finisher))<br>
 * .forEach(System.out::println);
 *
 * @author Thomas Freese
 */
public class MavenUpdatesCollectorSimple extends AbstractMavenUpdatesCollector {
    private final List<Coordinate> updates;

    /**
     * Stellt den Supplier mit dem das collect() das Zielobjekt ({@link MavenUpdatesCollectorSimple}) erzeugen kann.
     */
    public MavenUpdatesCollectorSimple() {
        super();

        updates = new ArrayList<>();
    }

    /**
     * Stellt den Accumulator mit dem das collect() ein Element aus dem Stream in dem Zielobjekt ({@link MavenUpdatesCollectorSimple}) sammeln soll.
     */
    public void accumulate(final String s) {
        doAccumulate(updates, s);
    }

    /**
     * Stellt den Combiner mit dem das collect() zwei Zielobjekte ({@link MavenUpdatesCollectorSimple}) zu einem zusammenfassen soll.<br>
     * Wird nur bei parallelen Streams benötigt.
     */
    public MavenUpdatesCollectorSimple combine(final MavenUpdatesCollectorSimple right) {
        final List<Coordinate> list = doCombine(updates, right.updates);

        final MavenUpdatesCollectorSimple muc = new MavenUpdatesCollectorSimple();
        muc.updates.addAll(list);

        return muc;
    }

    /**
     * Stellt den Finisher/Finalizer mit dem das collect() das Endergebnis aus dem Zielobjekt ({@link MavenUpdatesCollectorSimple}) liefert.
     */
    public Stream<Coordinate> finisher() {
        return doFinish(updates);
    }
}
