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
        setColorModel(colorModel);
    }

    public ColorModel getColorModel() {
        return this.colorModel;
    }

    public abstract String getDisplayValue();

    public void setColorModel(ColorModel colorModel) {
        if (colorModel == null) {
            colorModel = new DefaultColorModel();
        }

        this.colorModel = colorModel;
    }

    public abstract void setValue(Object obj);
}
