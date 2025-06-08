// Created: 06.11.2013
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public class VendingMaschine {
    private final Box[] boxes;
    private final CashBox cashBox;

    public VendingMaschine(final CashBox cashBox, final Box[] boxes) {
        super();

        this.cashBox = cashBox;
        this.boxes = boxes;
    }

    public void selectItem(final int boxIndex) throws Exception {
        final Box box = boxes[boxIndex];

        if (box.isEmpty()) {
            throw new Exception("box is empty");
        }

        final int amountRequired = box.getPrice();

        if (amountRequired > cashBox.getCurrentAmount()) {
            throw new Exception("not enough money");
        }

        box.releaseItem();
        cashBox.withdraw(amountRequired);
    }
}
