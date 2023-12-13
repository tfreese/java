package net.led.elements;

import java.awt.Color;

import net.led.tokens.ArrowToken;
import net.led.tokens.NumberToken;
import net.led.tokens.TextToken;
import net.led.tokens.Token;

/**
 * This is a sample element that extends <code>AbstractDisplayElement</code> - the abstract class that represents a display's text base element.<br>
 * The purpose of this element is to hold stock quote information: name, last value, trend and percent change. The trend is given by the percent change relative
 * to 0.
 */
public class StockDisplayElement extends AbstractDisplayElement {
    private final ArrowToken arrow;

    private final NumberToken changePercent;

    private final NumberToken last;

    private final StockColorModel stockColorModel;

    private final TextToken symbol;

    public StockDisplayElement(final String stock) {
        super(new Token[4]);

        this.symbol = new TextToken(new DefaultColorModel(new Color(0xffffff)), stock);
        this.stockColorModel = new StockColorModel();
        this.last = new NumberToken(this.stockColorModel);
        this.arrow = new ArrowToken(this.stockColorModel);
        this.changePercent = new NumberToken(this.stockColorModel);

        this.getTokens()[0] = this.symbol;
        this.getTokens()[1] = this.last;
        this.getTokens()[2] = this.arrow;
        this.getTokens()[3] = this.changePercent;
    }

    public String getSymbol() {
        return this.symbol.getDisplayValue();
    }

    public void setChangePercent(final Double change) {
        this.changePercent.setValue(change);
        this.stockColorModel.setChangePercent(change);

        if (change > 0D) {
            this.arrow.setValue(ArrowToken.INCREASING);
        }
        else if (change < 0D) {
            this.arrow.setValue(ArrowToken.DECREASING);
        }
        else {
            this.arrow.setValue(ArrowToken.UNCHANGED);
        }
    }

    public void setLast(final Double lastValue) {
        this.last.setValue(lastValue);
    }

    public void setStockDownColor(final Color c) {
        this.stockColorModel.setDownColor(c);
    }

    public void setStockNeutralColor(final Color c) {
        this.stockColorModel.setNeutralColor(c);
    }

    public void setStockUpColor(final Color c) {
        this.stockColorModel.setUpColor(c);
    }

    public void setSymbolColor(final Color c) {
        this.symbol.getColorModel().setColor(c);
    }
}
