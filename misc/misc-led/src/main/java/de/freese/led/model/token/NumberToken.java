// Created: 20.12.23
package de.freese.led.model.token;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Thomas Freese
 */
public class NumberToken extends AbstractLedToken {
    private static NumberFormat getDefaultNumberFormat() {
        final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(3);

        return nf;
    }

    private final NumberFormat numberFormat;

    private String value = "N/A";

    public NumberToken() {
        this(getDefaultNumberFormat());
    }

    public NumberToken(final Number number) {
        this(number, getDefaultNumberFormat());
    }

    public NumberToken(final NumberFormat numberFormat) {
        this(null, numberFormat);
    }

    public NumberToken(final Number number, final NumberFormat numberFormat) {
        super();

        this.numberFormat = numberFormat;

        setValue(number);
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(final Number number) {
        if (number instanceof Double d && (Double.isNaN(d) || Double.isInfinite(d))) {
            this.value = "N/A";
        }
        else if (number == null) {
            this.value = "N/A";
        }
        else {
            this.value = this.numberFormat.format(number);
        }
    }
}
