package net.led.elements;

import java.awt.Color;

import net.led.tokens.DateToken;
import net.led.tokens.TextToken;
import net.led.tokens.TimeToken;
import net.led.tokens.Token;

/**
 * @author Thomas Freese
 */
public class TimeDisplayElement extends AbstractDisplayElement {
    private final DateToken date;
    private final TextToken text;
    private final TimeToken time;

    public TimeDisplayElement(final String attributName) {
        super(new Token[3]);

        this.text = new TextToken(new DefaultColorModel(new Color(0xffffff)), attributName);
        this.date = new DateToken();
        this.time = new TimeToken();

        this.getTokens()[0] = this.text;
        this.getTokens()[1] = this.date;
        this.getTokens()[2] = this.time;
    }

    public void setTime(final Object newValue) {
        this.date.setValue(newValue);
        this.time.setValue(newValue);
    }
}
