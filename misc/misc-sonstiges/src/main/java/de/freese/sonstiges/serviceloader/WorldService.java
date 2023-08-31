// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

/**
 * BeispielService.
 *
 * @author Thomas Freese
 */
public class WorldService implements Service {
    @Override
    public String getText() {
        return "World";
    }
}
