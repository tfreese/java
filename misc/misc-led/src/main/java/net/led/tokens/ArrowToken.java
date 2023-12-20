package net.led.tokens;

import net.led.elements.ColorModel;

/**
 * @author Thomas Freese
 */
public final class ArrowToken extends Token {
    public enum ArrowForm {
        DECREASING,
        INCREASING,
        UNCHANGED
    }

    private ArrowForm form = ArrowForm.UNCHANGED;

    public ArrowToken() {
        super(null);
    }

    public ArrowToken(final ArrowForm form) {
        super(null);

        this.form = form;
    }

    public ArrowToken(final ColorModel colorModel) {
        super(colorModel);
    }

    public ArrowForm getArrowForm() {
        return this.form;
    }

    @Override
    public String getDisplayValue() {
        return "";
    }

    @Override
    public void setValue(final Object form) {
        this.form = (ArrowForm) form;
    }
}
