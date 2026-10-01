package de.freese.jconky.sensor.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import de.freese.jconky.sensor.AbstractSensor;

/**
 * @author Thomas Freese
 * @since 01.10.26
 */
public final class ExternalIpSensor extends AbstractSensor {
    private String externalIp = "";

    public String getExternalIp() {
        return externalIp;
    }

    @Override
    public void update() {
        try {
            // final URL url = URI.create("https://ifconfig.me/ip").toURL();
            final URL url = URI.create("https://4.ident.me").toURL();
            final URLConnection connection = url.openConnection();
            // connection.connect();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                externalIp = br.readLine();
            }
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
