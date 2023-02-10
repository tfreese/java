// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

/**
 * BeispielService.
 *
 * @author Thomas Freese
 */
public class HelloService implements Service {
    /**
     * @see Service#getText()
     */
    @Override
    public String getText() {
        return "Hello";
    }
}
