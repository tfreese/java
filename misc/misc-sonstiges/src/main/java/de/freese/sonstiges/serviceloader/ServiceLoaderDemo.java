// Created: 17.07.2011
package de.freese.sonstiges.serviceloader;

import java.util.ServiceLoader;

/**
 * Demo.
 *
 * @author Thomas Freese
 */
public class ServiceLoaderDemo
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        ServiceLoader<Service> serviceLoader = ServiceLoader.load(Service.class);

        for (Service service : serviceLoader)
        {
            System.out.println(service.getText());
        }
    }
}
