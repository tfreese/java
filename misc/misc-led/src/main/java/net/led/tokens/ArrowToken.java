package net.led.tokens;

import net.led.elements.ColorModel;

/**
 * @author Thomas Freese
 */
public final class ArrowToken extends Token {
    public static final Object DECREASING = new Object();

    public static final Object INCREASING = new Object();

    public static final Object UNCHANGED = new Object();

    private Object value;

    public ArrowToken() {
        this(null);
    }

    public ArrowToken(final ColorModel colorModel) {
        super(colorModel);

        setValue(UNCHANGED);
    }

    public Object getArrowType() {
        return this.value;
    }

    @Override
    public String getDisplayValue() {
        return "";
    }

    @Override
    public void setValue(final Object value) {
        this.value = value;
    }
}
