package net.led.tokens;

import net.led.elements.ColorModel;

/**
 * A token representing a line of text
 *
 * @version 1.0 12/14/04
 */
public class TextToken extends Token {
    private String text;

    public TextToken() {
        this(null, "");
    }

    public TextToken(final String text) {
        this(null, text);
    }

    public TextToken(final ColorModel colorModel, final String text) {
        super(colorModel);

        this.text = text;
    }

    @Override
    public String getDisplayValue() {
        return this.text;
    }

    @Override
    public void setValue(final Object value) {
        if (value == null) {
            throw new NullPointerException("Given value cannot be null");
        }

        if (value instanceof String s) {
            this.text = s;
            return;
        }

        throw new IllegalArgumentException("Given value must be a String, not " + value.getClass().getName());
    }
}
