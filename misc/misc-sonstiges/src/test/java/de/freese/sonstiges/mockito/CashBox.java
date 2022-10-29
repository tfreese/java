// Created: 06.11.2013
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public interface CashBox
{
    /**
     * @return int
     */
    int getCurrentAmount();

    /**
     * @param amountRequired int
     */
    void withdraw(int amountRequired);
}
