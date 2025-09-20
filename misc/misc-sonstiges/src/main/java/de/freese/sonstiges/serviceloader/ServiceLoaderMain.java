// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class ServiceLoaderMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoaderMain.class);

    static void main() {
        final ServiceLoader<Service> serviceLoader = ServiceLoader.load(Service.class);

        for (Service service : serviceLoader) {
            LOGGER.info(service.getText());
        }
    }

    private ServiceLoaderMain() {
        super();
    }
}
