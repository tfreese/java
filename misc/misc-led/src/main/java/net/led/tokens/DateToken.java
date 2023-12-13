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
public class DateToken extends Token {
    /**
     * The number representation's format
     */
    private DateFormat dateFormatter;
    /**
     * The representation of the number.
     */
    private String displayValue;

    public DateToken() {
        this(null);
    }

    public DateToken(final ColorModel colorModel) {
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
        this.displayValue = getDateFormatter().format(date);
    }

    private DateFormat getDateFormatter() {
        if (this.dateFormatter == null) {
            this.dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        }

        return this.dateFormatter;
    }
}
