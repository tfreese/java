package net.led.util;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ColorSelectorListener {
    void setColor(String id, Color color);
}
