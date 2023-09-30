package net.led.provider;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface UpdateListener {
    void update(Object newValue);
}
