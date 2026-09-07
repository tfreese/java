package de.freese.sonstiges.serviceloader;

/**
 * @author Thomas Freese
 * @since 17.07.2011
 */
public class HelloService implements Service {
    @Override
    public String getText() {
        return "Hello";
    }
}
