// Created: 29.06.2020
package de.freese.ga.examples.travelling_salesman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.freese.ga.Chromosome;
import de.freese.ga.Gene;

/**
 * @author Thomas Freese
 */
public class TspChromosome extends Chromosome {
    public TspChromosome(final TspConfig config) {
        super(config);
    }

    @Override
    public double calcFitnessValue() {
        return 1.0D / getDistance();
    }

    /**
     * Gets the total distance of the tour.
     */
    public double getDistance() {
        double tourDistance = 0.0D;

        // Loop through our tour's cities.
        for (int i = 0; i < size(); i++) {
            // Get city we're travelling from
            final City fromCity = getGene(i).getValue(City.class);

            // City we're travelling to.
            City destinationCity = null;

            // Check we're not on our tour's last city, if we are set our
            // tour's final destination city to our starting city
            if ((i + 1) < size()) {
                destinationCity = getGene(i + 1).getValue(City.class);
            }
            else {
                destinationCity = getGene(0).getValue(City.class);
            }

            // Get the distance between the two cities
            tourDistance += fromCity.distanceTo(destinationCity);
        }

        return tourDistance;
    }

    @Override
    public void populate() {
        final List<City> cities = getConfig().getCities();

        final List<Gene> genes = new ArrayList<>();

        // Loop through all our destination cities and add them to our tour
        for (int i = 0; i < size(); i++) {
            genes.add(Gene.of(cities.get(i)));
        }

        // Zufällig neu zusammenstellen.
        Collections.shuffle(genes);

        for (int i = 0; i < size(); i++) {
            setGene(i, genes.get(i));
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Stream.of(getGenes())
               .map(g -> (City) g.getValue())
               .map(City::name)
               .collect(Collectors.joining());
        // @formatter:on
    }

    @Override
    protected TspConfig getConfig() {
        return (TspConfig) super.getConfig();
    }
}
