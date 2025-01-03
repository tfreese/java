// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

/**
 * @author Thomas Freese
 */
public class HelloService implements Service {
    @Override
    public String getText() {
        return "Hello";
    }
}
