package de.freese.led.model.token;

import java.awt.Color;

/**
 * @author Thomas Freese
 * @since 20.12.23
 */
public class TextToken extends AbstractLedToken {
    private String value;

    public TextToken() {
        super();
    }

    public TextToken(final String value) {
        this(value, null);
    }

    public TextToken(final String value, final Color color) {
        super(color);

        this.value = value;
    }

    @Override
    public String getValue() {
        return value == null ? "N/A" : value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
