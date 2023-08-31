// Created: 01.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class HostInfo {
    private final String architecture;

    private final String name;

    private final String version;

    public HostInfo() {
        this("", "", "");
    }

    public HostInfo(final String name, final String version, final String architecture) {
        super();

        this.name = name;
        this.version = version;
        this.architecture = architecture;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("name=").append(this.name);
        builder.append(", version=").append(this.version);
        builder.append(", architecture=").append(this.architecture);
        builder.append("]");

        return builder.toString();
    }
}
