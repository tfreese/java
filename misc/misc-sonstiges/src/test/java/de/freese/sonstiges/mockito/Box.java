// Created: 06.11.2013
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public interface Box
{
    /**
     * @return int
     */
    int getPrice();

    /**
     * @return boolean
     */
    boolean isEmpty();

    /**
     *
     */
    void releaseItem();
}
