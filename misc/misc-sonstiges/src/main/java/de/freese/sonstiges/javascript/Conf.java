// Created: 10.10.2012
package de.freese.sonstiges.javascript;

/**
 * @author Thomas Freese
 */
public class Conf
{
    /**
     *
     */
    private int blockSize;
    /**
     *
     */
    private int threads;

    /**
     * @return int
     */
    public int getBlockSize()
    {
        return this.blockSize;
    }

    /**
     * @return int
     */
    public int getThreads()
    {
        return this.threads;
    }

    /**
     * @param blockSize int
     */
    public void setBlockSize(final int blockSize)
    {
        this.blockSize = blockSize;
    }

    /**
     * @param threads int
     */
    public void setThreads(final int threads)
    {
        this.threads = threads;
    }
}
