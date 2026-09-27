package de.freese.jconky.sensor.host;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public record Host(String name, String version, String architecture) {
    public Host() {
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
