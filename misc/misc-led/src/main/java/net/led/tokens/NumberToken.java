package net.led.tokens;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import net.led.elements.ColorModel;

/**
 * A token representing a number
 *
 * @version 1.0 12/14/04
 */
public class NumberToken extends Token {
    private static NumberFormat getDefaultNumberFormat() {
        final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(3);

        return nf;
    }

    /**
     * The number representation's format
     */
    private final NumberFormat numberFormat;
    /**
     * The representation of the number.
     */
    private String displayValue;

    public NumberToken() {
        this(null, null);
    }

    public NumberToken(final ColorModel colorModel) {
        this(colorModel, null);
    }

    public NumberToken(final NumberFormat numberFormat) {
        this(null, numberFormat);
    }

    public NumberToken(final ColorModel colorModel, final NumberFormat numberFormat) {
        super(colorModel);

        this.numberFormat = Objects.requireNonNullElse(numberFormat, getDefaultNumberFormat());

        formatDisplayValue(Double.NaN);
    }

    @Override
    public String getDisplayValue() {
        return this.displayValue;
    }

    @Override
    public void setValue(final Object newValue) {
        if (newValue instanceof Number number) {
            formatDisplayValue(number.doubleValue());

            return;
        }

        throw new IllegalArgumentException("Given value must be a java.lang.Number, not " + newValue.getClass().getName());
    }

    private void formatDisplayValue(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            this.displayValue = "N/A";
        }
        else {
            this.displayValue = this.numberFormat.format(value);
        }
    }
}
