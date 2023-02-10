// Created: 04.03.2021
package de.freese.simulationen.model;

/**
 * @author Thomas Freese
 */
public enum SimulationType {
    ANTS("ants"),
    BOUNCING_BALLS("balls"),
    GAME_OF_LIFE("gof"),
    WATER_TORUS("wator");

    public static SimulationType findByNameShort(final String nameShort) {
        for (SimulationType type : values()) {
            if (type.getNameShort().equals(nameShort)) {
                return type;
            }
        }

        throw new IllegalArgumentException(String.format("'%s' not found", nameShort));
    }

    private final String nameShort;

    SimulationType(final String nameShort) {
        this.nameShort = nameShort;
    }

    public String getNameShort() {
        return this.nameShort;
    }
}
