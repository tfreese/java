package net.led.tokens;

import net.led.elements.ColorModel;
import net.led.elements.DefaultColorModel;

/**
 * @author Thomas Freese
 */
public abstract class Token {
    private ColorModel colorModel;

    protected Token() {
        this(new DefaultColorModel());
    }

    protected Token(final ColorModel colorModel) {
        super();

        this.colorModel = colorModel;
    }

    public ColorModel getColorModel() {
        if (colorModel == null) {
            colorModel = new DefaultColorModel();
        }

        return colorModel;
    }

    public abstract String getDisplayValue();

    public void setColorModel(final ColorModel colorModel) {
        this.colorModel = colorModel;
    }

    public abstract void setValue(Object obj);
}
