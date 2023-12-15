package net.ledticker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import net.led.elements.Element;
import net.led.tokens.ArrowToken;
import net.led.tokens.Token;

/**
 * @author Thomas Freese
 */
public class Matrix {
    protected static final Map<Object, byte[]> map;

    static {
        map = new HashMap<>();
        map.put(" ", new byte[]{0, 0, 0, 0, 0});
        map.put("A", new byte[]{126, 9, 9, 9, 126});
        map.put("a", new byte[]{32, 84, 84, 84, 120});
        map.put("B", new byte[]{127, 73, 73, 73, 62});
        map.put("b", new byte[]{127, 68, 68, 68, 56});
        map.put("C", new byte[]{62, 65, 65, 65, 34});
        map.put("c", new byte[]{56, 68, 68, 68, 0});
        map.put("D", new byte[]{65, 127, 65, 65, 62});
        map.put("d", new byte[]{56, 68, 68, 72, 127});
        map.put("E", new byte[]{127, 73, 73, 65, 65});
        map.put("e", new byte[]{56, 84, 84, 84, 24});
        map.put("F", new byte[]{127, 9, 9, 1, 1});
        map.put("f", new byte[]{8, 126, 9, 1, 2});
        map.put("G", new byte[]{62, 65, 65, 73, 58});
        map.put("g", new byte[]{72, 84, 84, 84, 60});
        map.put("H", new byte[]{127, 8, 8, 8, 127});
        map.put("h", new byte[]{127, 8, 4, 4, 120});
        map.put("I", new byte[]{0, 65, 127, 65, 0});
        map.put("i", new byte[]{0, 68, 125, 64, 0});
        map.put("J", new byte[]{32, 64, 65, 63, 1});
        map.put("j", new byte[]{32, 64, 68, 61, 0});
        map.put("K", new byte[]{127, 8, 20, 34, 65});
        map.put("k", new byte[]{127, 16, 40, 68, 0});
        map.put("L", new byte[]{127, 64, 64, 64, 64});
        map.put("l", new byte[]{0, 65, 127, 64, 0});
        map.put("M", new byte[]{127, 2, 12, 2, 127});
        map.put("m", new byte[]{124, 4, 24, 4, 120});
        map.put("N", new byte[]{127, 4, 8, 16, 127});
        map.put("n", new byte[]{124, 8, 4, 4, 120});
        map.put("O", new byte[]{62, 65, 65, 65, 62});
        map.put("o", new byte[]{56, 68, 68, 68, 56});
        map.put("P", new byte[]{127, 9, 9, 9, 6});
        map.put("p", new byte[]{124, 20, 20, 20, 8});
        map.put("Q", new byte[]{62, 65, 81, 33, 94});
        map.put("q", new byte[]{8, 20, 20, 20, 124});
        map.put("R", new byte[]{127, 9, 25, 41, 70});
        map.put("r", new byte[]{124, 8, 4, 4, 8});
        map.put("S", new byte[]{38, 73, 73, 73, 50});
        map.put("s", new byte[]{72, 84, 84, 84, 32});
        map.put("T", new byte[]{1, 1, 127, 1, 1});
        map.put("t", new byte[]{4, 63, 68, 64, 64});
        map.put("U", new byte[]{63, 64, 64, 64, 63});
        map.put("u", new byte[]{60, 64, 64, 32, 124});
        map.put("V", new byte[]{7, 24, 96, 24, 7});
        map.put("v", new byte[]{28, 32, 64, 32, 28});
        map.put("W", new byte[]{127, 32, 24, 32, 127});
        map.put("w", new byte[]{60, 64, 48, 64, 60});
        map.put("X", new byte[]{99, 20, 8, 20, 99});
        map.put("x", new byte[]{68, 40, 16, 40, 68});
        map.put("Y", new byte[]{7, 8, 120, 8, 7});
        map.put("y", new byte[]{12, 80, 80, 80, 60});
        map.put("Z", new byte[]{97, 81, 73, 69, 67});
        map.put("z", new byte[]{68, 100, 84, 76, 68});
        map.put("0", new byte[]{62, 81, 73, 69, 62});
        map.put("1", new byte[]{0, 66, 127, 64, 0});
        map.put("2", new byte[]{98, 81, 81, 73, 70});
        map.put("3", new byte[]{34, 65, 73, 73, 54});
        map.put("4", new byte[]{24, 20, 18, 127, 16});
        map.put("5", new byte[]{39, 69, 69, 69, 57});
        map.put("6", new byte[]{60, 74, 73, 73, 49});
        map.put("7", new byte[]{1, 113, 9, 5, 3});
        map.put("8", new byte[]{54, 73, 73, 73, 54});
        map.put("9", new byte[]{70, 73, 73, 41, 30});
        map.put("~", new byte[]{2, 1, 2, 4, 2});
        map.put("`", new byte[]{1, 2, 4, 0, 0});
        map.put("!", new byte[]{0, 0, 111, 0, 0});
        map.put("@", new byte[]{62, 65, 93, 85, 14});
        map.put("#", new byte[]{20, 127, 20, 127, 20});
        map.put("$", new byte[]{44, 42, 127, 42, 26});
        map.put("%", new byte[]{38, 22, 8, 52, 50});
        map.put("^", new byte[]{4, 2, 1, 2, 4});
        map.put("&", new byte[]{54, 73, 86, 32, 80});
        map.put("*", new byte[]{42, 28, 127, 28, 42});
        map.put("(", new byte[]{0, 0, 62, 65, 0});
        map.put(")", new byte[]{0, 65, 62, 0, 0});
        map.put("-", new byte[]{8, 8, 8, 8, 8});
        map.put("_", new byte[]{64, 64, 64, 64, 64});
        map.put("+", new byte[]{8, 8, 127, 8, 8});
        map.put("=", new byte[]{36, 36, 36, 36, 36});
        map.put("\\", new byte[]{3, 4, 8, 16, 96});
        map.put("|", new byte[]{0, 0, 127, 0, 0});
        map.put("{", new byte[]{0, 8, 54, 65, 65});
        map.put("}", new byte[]{65, 65, 54, 8, 0});
        map.put("[", new byte[]{0, 127, 65, 65, 0});
        map.put("]", new byte[]{0, 65, 65, 127, 0});
        map.put(":", new byte[]{0, 0, 54, 54, 0});
        map.put(";", new byte[]{0, 91, 59, 0, 0});
        map.put(",", new byte[]{0, 0, 88, 56, 0});
        map.put(".", new byte[]{0, 96, 96, 0, 0});
        map.put("<", new byte[]{8, 20, 34, 65, 0});
        map.put(">", new byte[]{65, 34, 20, 8, 0});
        map.put("?", new byte[]{2, 1, 89, 5, 2});
        map.put("/", new byte[]{96, 16, 8, 4, 3});
        map.put("'", new byte[]{0, 0, 7, 0, 0});
        map.put("\"", new byte[]{0, 7, 0, 7, 0});
        map.put(ArrowToken.INCREASING, new byte[]{16, 24, 28, 24, 16});
        map.put(ArrowToken.UNCHANGED, new byte[]{8, 28, 28, 28, 8});
        map.put(ArrowToken.DECREASING, new byte[]{4, 12, 28, 12, 4});
    }

    private final int bottomInset;
    private final int topInset;

    private Color backgroundColor;
    private int dotHeight;
    private Color dotOffColor;
    private int dotWidth;
    private int elementGap;
    private int hGap;
    private int tokenGap;
    private int vGap;

    public Matrix() {
        super();

        this.tokenGap = 2;
        this.elementGap = 4;
        this.hGap = 1;
        this.vGap = 1;
        this.dotHeight = 1;
        this.dotWidth = 1;
        this.topInset = 1;
        this.bottomInset = 1;
        this.backgroundColor = new Color(1118481);
        this.dotOffColor = new Color(6710886);
    }

    public int getHeight() {
        return ((this.topInset + this.bottomInset + 7) * (this.dotHeight + this.vGap)) - this.vGap;
    }

    public Image getImage() {
        final int height = getHeight();
        final int width = 10 * (this.hGap + this.dotWidth);

        final BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        paintDots(bufferedimage.getGraphics(), width, height);

        return bufferedimage;
    }

    public int getWidthOf(final Element tickerElement) {
        int width = 0;
        final Token[] tokens = tickerElement.getTokens();

        for (int i = 0; i < tokens.length; i++) {
            width += getWidth(tokens[i]);
            width += ((i == (tokens.length - 1)) ? 0 : (this.tokenGap * (this.hGap + this.dotWidth)));
        }

        width += (this.elementGap * (this.hGap + this.dotWidth));

        if ((width % (this.hGap + this.dotWidth)) != 0) {
            width += ((this.hGap + this.dotWidth) - (width % (this.hGap + this.dotWidth)));
        }

        return width;
    }

    public void paintDots(final Graphics graphics, final int width, final int height) {
        graphics.setColor(this.backgroundColor);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(this.dotOffColor);

        for (int y = 0; y < height; y += this.dotHeight) {
            graphics.fillRect(0, y, width, this.dotHeight);
            y += this.vGap;
        }

        graphics.setColor(this.backgroundColor);

        for (int x = this.dotWidth; x < width; x += this.dotWidth) {
            graphics.fillRect(x, 0, this.hGap, height);
            x += this.hGap;
        }
    }

    public void paintElement(final Graphics graphics, final Element tickerElement) {
        final Token[] tokens = tickerElement.getTokens();
        int x = 0;

        for (Token token : tokens) {
            x = paintToken(graphics, token, x);
        }
    }

    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setDotGaps(final int hGap, final int vGap) {
        this.hGap = hGap;
        this.vGap = vGap;
    }

    public void setDotOffColor(final Color dotOffColor) {
        this.dotOffColor = dotOffColor;
    }

    public void setDotSize(final int dotWidth, final int dotHeight) {
        this.dotWidth = dotWidth;
        this.dotHeight = dotHeight;
    }

    public void setElementGap(final int elementGap) {
        this.elementGap = elementGap;
    }

    public void setTokenGap(final int tokenGap) {
        this.tokenGap = tokenGap;
    }

    protected Image getDefaultImage() {
        final int height = getHeight();
        final int width = 10 * (this.hGap + this.dotWidth);

        final BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        paintDots(bufferedimage.getGraphics(), width, height);

        final String s = "WWW.LEDTICKER.NET::";
        int x = 0;
        final Graphics graphics = bufferedimage.getGraphics();

        for (int i = 0; i < s.length(); i++) {
            byte[] bytes = map.get(String.valueOf(s.charAt(i)));

            if (bytes == null) {
                bytes = map.get("?");
            }

            x = paint(graphics, bytes, x);
        }

        return bufferedimage;
    }

    private int getWidth(final Token token) {
        final int width = 6 * (this.dotWidth + this.hGap);

        if (token instanceof ArrowToken) {
            return width;
        }

        return token.getDisplayValue().length() * width;
    }

    private int paint(final Graphics graphics, final byte[] bytes, final int x) {
        final Color color = graphics.getColor();

        int mX = x;

        for (byte b : bytes) {
            for (int j = 0; j < 7; j++) {
                if ((b & (1 << j)) != 0) {
                    graphics.setColor(color);
                    final int y = (j + this.topInset) * (this.dotHeight + this.vGap);
                    graphics.fillRect(mX, y, this.dotWidth, this.dotHeight);
                }
            }

            mX += (this.dotWidth + this.hGap);
        }

        mX += (this.hGap + this.dotWidth);
        graphics.setColor(color);

        return mX;
    }

    private int paintToken(final Graphics graphics, final Token token, final int x) {
        final Color color = token.getColorModel().getColor();
        graphics.setColor(color);

        int mX = x;

        if (token instanceof ArrowToken arrowToken) {
            final byte[] bytes = map.get(arrowToken.getArrowType());
            mX = paint(graphics, bytes, mX);
        }
        else {
            final String s = token.getDisplayValue();

            for (int i = 0; i < s.length(); i++) {
                byte[] bytes = map.get(String.valueOf(s.charAt(i)));

                if (bytes == null) {
                    bytes = map.get("?");
                }

                mX = paint(graphics, bytes, mX);
            }
        }

        mX += (this.tokenGap * (this.hGap + this.dotWidth));

        return mX;
    }
}
