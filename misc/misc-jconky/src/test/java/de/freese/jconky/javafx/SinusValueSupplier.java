// Created: 24.11.2020
package de.freese.jconky.javafx;

import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public class SinusValueSupplier implements Supplier<Float> {

    private int grad;

    @Override
    public Float get() {
        //        double radian = this.grad * (Math.PI / 180D);
        double radian = Math.toRadians(this.grad);
        double sinus = Math.sin(radian);

        this.grad += 1;

        if (this.grad > 360) {
            this.grad = 0;
        }

        return (float) sinus;
    }
}
