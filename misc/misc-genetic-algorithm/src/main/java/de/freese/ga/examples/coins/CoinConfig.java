// Created: 29.06.2020
package de.freese.ga.examples.coins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.freese.ga.Config;

/**
 * @author Thomas Freese
 */
public class CoinConfig extends Config {
    private final List<Integer> existingCoins = new ArrayList<>();
    /**
     * Enthält die Anzahl von Münzen pro Wert.<br>
     * Key = Münze, Value = Anzahl
     */
    private Map<Integer, Long> coinCounter = new HashMap<>();
    private int targetCents;

    @Override
    public double getMaxFitness() {
        // Keine Lösung bekannt.
        return 1_000D;
    }

    public void setExistingCoins(final List<Integer> existingCoins) {
        this.coinCounter.clear();
        this.existingCoins.clear();
        this.existingCoins.addAll(existingCoins);

        // 0 Münzen, falls wir nicht so viele brauchen, wie die das Chromosom lang ist.
        for (int i = 0; i < existingCoins.size(); i++) {
            this.existingCoins.add(0);
        }

        setSizeChromosome(this.existingCoins.size());

        // Anzahl Münzen pro Wert zählen.
        final List<Integer> list = new ArrayList<>(this.existingCoins);
        list.removeIf(value -> value == 0); // 0-Münzen ignorieren

        this.coinCounter = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public void setTargetCents(final int cents) {
        if (cents <= 0 || cents > getMaximumCents()) {
            throw new IllegalArgumentException("cents must be between 0 - " + getMaximumCents() + ": " + cents);
        }

        this.targetCents = cents;
    }

    Map<Integer, Long> getCoinCounter() {
        return this.coinCounter;
    }

    List<Integer> getExistingCoins() {
        return this.existingCoins;
    }

    int getMaximumCents() {
        return 99;
    }

    int getTargetCents() {
        return this.targetCents;
    }
}
