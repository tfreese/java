// Created: 01.09.2015
package de.freese.ga;

import java.util.Objects;

/**
 * Basisklasse eines Genoms / Gens für genetische Algorithmen.
 *
 * @author Thomas Freese
 */
public class Gene implements Comparable<Gene>
{
    private Object value;

    public Gene()
    {
        super();
    }

    public Gene(final Object value)
    {
        super();

        setValue(value);
    }

    /**
     * Selber Typ und selber Wert.
     */
    @Override
    public int compareTo(final Gene o)
    {
        if ((o == null) || (o == this) || (getClass() != o.getClass()))
        {
            return 0;
        }

        int comp = 0;

        if (getValue() instanceof Comparable)
        {
            comp = ((Comparable<?>) getValue()).compareTo(o.getValue());
        }
        else
        {
            throw new IllegalStateException("GeneValue must implement Comparable");
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof Gene gene))
        {
            return false;
        }

        return Objects.equals(getValue(), gene.getValue());
    }

    public Boolean getBoolean()
    {
        return getValue();
    }

    public Double getDouble()
    {
        return getValue();
    }

    public Integer getInteger()
    {
        return getValue();
    }

    public <T> T getValue()
    {
        return (T) this.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getValue());
    }

    public void setValue(final Object value)
    {
        this.value = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(Objects.toString(getValue(), "null"));

        return sb.toString();
    }
}
