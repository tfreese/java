// Created: 01.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public record HostInfo(String name, String version, String architecture) {
    public HostInfo() {
        this("", "", "");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + "name=" + name
                + ", version=" + version
                + ", architecture=" + architecture
                + "]";
    }
}
