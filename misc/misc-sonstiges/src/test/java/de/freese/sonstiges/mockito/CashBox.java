package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 * @since 06.11.2013
 */
public interface CashBox {
    int getCurrentAmount();

    void withdraw(int amountRequired);
}
