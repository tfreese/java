package net.led.tokens;

import java.text.NumberFormat;
import java.util.Locale;

import net.led.elements.ColorModel;

/**
 * A token representing a number
 *
 * @version 1.0 12/14/04
 */
public class NumberToken extends Token {
    /**
     * The representation of the number.
     */
    private String displayValue;
    /**
     * The number representation's format
     */
    private NumberFormat numberFormat;
    /**
     * The represented value
     */
    private double value;

    public NumberToken() {
        this((NumberFormat) null);
    }

    public NumberToken(final ColorModel colorModel) {
        this();

        setColorModel(colorModel);
    }

    public NumberToken(final NumberFormat numberFormat) {
        this.value = Double.NaN;

        setNumberFormat(numberFormat);
    }

    public NumberToken(final NumberFormat numberFormat, final ColorModel colorModel) {
        this(numberFormat);

        setColorModel(colorModel);
    }

    @Override
    public String getDisplayValue() {
        return this.displayValue;
    }

    public void setNumberFormat(NumberFormat newValue) {
        if (newValue == null) {
            newValue = getDefaultNumberFormat();
        }

        this.numberFormat = newValue;
        formatDisplayValue();
    }

    @Override
    public void setValue(final Object newValue) {
        if (newValue instanceof Number n) {
            this.value = n.doubleValue();
            formatDisplayValue();

            return;
        }

        throw new IllegalArgumentException("Given value must be a java.lang.Number, not " + newValue.getClass().getName());
    }

    private void formatDisplayValue() {
        if (Double.isNaN(this.value) || Double.isInfinite(this.value)) {
            this.displayValue = "N/A";
        }
        else {
            this.displayValue = this.numberFormat.format(this.value);
        }
    }

    private NumberFormat getDefaultNumberFormat() {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        return nf;
    }
}
