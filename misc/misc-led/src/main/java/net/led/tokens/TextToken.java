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
        this("");
    }

    public TextToken(final String newValue) {
        super();

        this.text = newValue;
    }

    public TextToken(final String text, final ColorModel colorModel) {
        this(text);

        setColorModel(colorModel);
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
