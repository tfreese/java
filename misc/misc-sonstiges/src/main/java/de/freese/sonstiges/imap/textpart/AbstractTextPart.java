package de.freese.sonstiges.imap.textpart;

/**
 * @author Thomas Freese
 */
public abstract class AbstractTextPart {

    private final String text;

    protected AbstractTextPart(final String text) {
        super();

        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
