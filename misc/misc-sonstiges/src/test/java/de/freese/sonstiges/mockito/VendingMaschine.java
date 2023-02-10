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
        Box box = this.boxes[boxIndex];

        if (box.isEmpty()) {
            throw new Exception("box is empty");
        }

        int amountRequired = box.getPrice();

        if (amountRequired > this.cashBox.getCurrentAmount()) {
            throw new Exception("not enough money");
        }

        box.releaseItem();
        this.cashBox.withdraw(amountRequired);
    }
}
