package de.freese.sonstiges.imap.textpart;

/**
 * @author Thomas Freese
 */
public abstract class AbstractTextPart
{
    /**
     *
     */
    private final String text;

    /**
     * Erstellt ein neues {@link AbstractTextPart} Object.
     *
     * @param text String
     */
    protected AbstractTextPart(final String text)
    {
        super();

        this.text = text;
    }

    /**
     * @return String
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return getText();
    }
}
