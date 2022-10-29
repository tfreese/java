// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

/**
 * BeispielService.
 *
 * @author Thomas Freese
 */
public class WorldService implements Service
{
    /**
     * @see Service#getText()
     */
    @Override
    public String getText()
    {
        return "World";
    }
}
