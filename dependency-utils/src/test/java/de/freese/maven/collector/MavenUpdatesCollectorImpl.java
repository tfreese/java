// Created: 30.12.2015
package de.freese.maven.collector;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import de.freese.dependency.update.coordinate.Coordinate;

/**
 * Klasse eines eigenen Collectors für Maven-UpdateResolver der Java-Streaming-API.<br>
 * readerIn.lines()<br>
 * .collect(new MavenUpdatesCollectorImpl())<br>
 * .collect(Collectors.toList());
 *
 * @author Thomas Freese
 */
public class MavenUpdatesCollectorImpl extends AbstractMavenUpdatesCollector implements Collector<String, List<Coordinate>, Stream<Coordinate>> {
    @Override
    public BiConsumer<List<Coordinate>, String> accumulator() {
        return this::doAccumulate;
    }

    /**
     * Nur mit UNORDERED, CONCURRENT oder einem leeren Set wird der Finisher aufgerufen.
     */
    @Override
    public Set<Collector.Characteristics> characteristics() {
        // return Collections.emptySet();
        return EnumSet.of(Characteristics.UNORDERED);
    }

    @Override
    public BinaryOperator<List<Coordinate>> combiner() {
        return this::doCombine;
    }

    @Override
    public Function<List<Coordinate>, Stream<Coordinate>> finisher() {
        return this::doFinish;
    }

    @Override
    public Supplier<List<Coordinate>> supplier() {
        return ArrayList::new;
    }
}
