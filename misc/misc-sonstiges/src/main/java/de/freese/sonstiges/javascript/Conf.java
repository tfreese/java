// Created: 10.10.2012
package de.freese.sonstiges.javascript;

/**
 * @author Thomas Freese
 */
public class Conf {
    private int blockSize;
    private int threads;

    public int getBlockSize() {
        return blockSize;
    }

    public int getThreads() {
        return threads;
    }

    public void setBlockSize(final int blockSize) {
        this.blockSize = blockSize;
    }

    public void setThreads(final int threads) {
        this.threads = threads;
    }
}
