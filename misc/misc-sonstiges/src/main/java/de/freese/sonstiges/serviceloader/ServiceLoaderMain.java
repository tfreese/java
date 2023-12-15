// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

import java.util.ServiceLoader;

/**
 * @author Thomas Freese
 */
public final class ServiceLoaderMain {
    public static void main(final String[] args) {
        final ServiceLoader<Service> serviceLoader = ServiceLoader.load(Service.class);

        for (Service service : serviceLoader) {
            System.out.println(service.getText());
        }
    }

    private ServiceLoaderMain() {
        super();
    }
}
