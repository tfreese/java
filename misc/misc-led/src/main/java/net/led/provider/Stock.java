package net.led.provider;

/**
 * @author Thomas Freese
 */
public class Stock {
    private final String id;
    private Double changePercent;
    private Double last;

    public Stock(final String id, final Double last, final Double changePercent) {
        this.id = id;
        this.last = last;
        this.changePercent = changePercent;
    }

    public Double getChangePercent() {
        return this.changePercent;
    }

    public String getID() {
        return this.id;
    }

    public Double getLast() {
        return this.last;
    }

    public void setChangePercent(final Double newValue) {
        this.changePercent = newValue;
    }

    public void setLast(final Double newValue) {
        this.last = newValue;
    }
}
