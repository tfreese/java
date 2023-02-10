// Created: 06.11.2013
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public interface CashBox {
    int getCurrentAmount();

    void withdraw(int amountRequired);
}
