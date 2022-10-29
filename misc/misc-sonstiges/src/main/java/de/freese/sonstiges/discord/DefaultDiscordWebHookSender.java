package de.freese.sonstiges.discord;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import de.freese.sonstiges.discord.message.DiscordWebHookMessage;

/**
 * @author Thomas Freese
 */
public class DefaultDiscordWebHookSender implements DiscordWebHookSender
{
    @Override
    public void send(final DiscordWebHookMessage message, final URI uri) throws IOException
    {
        String json = toJson(message);

        HttpsURLConnection connection = (HttpsURLConnection) uri.toURL().openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebHook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        try (OutputStream stream = connection.getOutputStream())
        {
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.flush();
        }

        connection.getInputStream().close(); // Wenn Fehler kommen, dann hier.
        connection.disconnect();
    }
}
