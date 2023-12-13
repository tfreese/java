package net.led.tokens;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.led.elements.ColorModel;

/**
 * A token representing a number
 *
 * @version 1.0 12/14/04
 */
public class TimeToken extends Token {
    /**
     * The representation of the number.
     */
    private String displayValue;
    /**
     * The number representation's format
     */
    private DateFormat timeFormatter;

    public TimeToken() {
        this(null);
    }

    public TimeToken(final ColorModel colorModel) {
        super(colorModel);

        formatDisplayValue(new Date());
    }

    @Override
    public String getDisplayValue() {
        return this.displayValue;
    }

    /**
     * @throws IllegalArgumentException if the given value is not a <tt>Number</tt>
     */
    @Override
    public void setValue(final Object newValue) {
        if (newValue instanceof Date date) {
            formatDisplayValue(date);

            return;
        }

        throw new IllegalArgumentException("Given value must be a java.lang.Number, not " + newValue.getClass().getName());
    }

    private void formatDisplayValue(final Date date) {
        this.displayValue = getTimeFormatter().format(date);
    }

    private DateFormat getTimeFormatter() {
        if (this.timeFormatter == null) {
            this.timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG, Locale.getDefault());
        }

        return this.timeFormatter;
    }
}
