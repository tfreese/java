package net.led.elements;

import java.awt.Color;

import net.led.tokens.TextToken;
import net.led.tokens.Token;

/**
 * @author Thomas Freese
 */
public class TextDisplayElement extends AbstractDisplayElement {
    private final TextToken textToken;

    public TextDisplayElement(final String text) {
        super(new Token[1]);

        this.textToken = new TextToken(text);
        this.getTokens()[0] = this.textToken;
    }

    public void setColor(final Color color) {
        this.textToken.getColorModel().setColor(color);
    }

    public void setText(final String text) {
        this.textToken.setValue(text);
    }
}
