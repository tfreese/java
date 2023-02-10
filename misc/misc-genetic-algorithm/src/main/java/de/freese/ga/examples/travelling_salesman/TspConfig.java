// Created: 29.06.2020
package de.freese.ga.examples.travelling_salesman;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.ga.Config;

/**
 * @author Thomas Freese
 */
public class TspConfig extends Config {
    private final List<City> cities = new ArrayList<>();

    public TspConfig() {
        super();
    }

    /**
     * @see de.freese.ga.Config#getMaxFitness()
     */
    @Override
    public double getMaxFitness() {
        // Keine Lösung bekannt.
        return Double.MAX_VALUE;
    }

    /**
     * Anzahl Städte = Anzahl Gene im Chromosom/Tour
     */
    public void setCities(final List<City> cities) {
        Objects.requireNonNull(cities, "cities required");

        this.cities.clear();
        this.cities.addAll(cities);

        setSizeChromosome(this.cities.size());
    }

    List<City> getCities() {
        return this.cities;
    }
}
