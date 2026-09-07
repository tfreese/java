package de.freese.openstreetmap;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * @author Thomas Freese
 * @since 07.09.26
 */
public final class UiUtils {
    public static void setGlobalFont(final Font font) {
        final FontUIResource fr = new FontUIResource(font);
        final Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            final Object value = UIManager.get(key);

            if (value instanceof FontUIResource) {
                UIManager.put(key, fr);
            }
        }
    }

    public static void setGlobalFontSize(final int newSize) {
        final Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            final Object value = UIManager.get(key);

            if (value instanceof final FontUIResource old) {
                UIManager.put(key, new FontUIResource(old.getFamily(), old.getStyle(), newSize));
            }
        }
    }

    private UiUtils() {
        super();
    }
}
