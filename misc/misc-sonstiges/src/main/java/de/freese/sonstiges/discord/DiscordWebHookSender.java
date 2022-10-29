// Created: 07.02.2021
package de.freese.sonstiges.discord;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.sonstiges.discord.message.DiscordWebHookMessage;

/**
 * Sendet eine Message an einen Discord-WebHook.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface DiscordWebHookSender
{
    /**
     * URL für den WebHook: Servereinstellungen -> Integrationen -> WebHooks anzeigen -> WebHook-URL kopieren
     *
     * @param message {@link DiscordWebHookMessage}
     * @param uri String
     *
     * @throws IOException Falls was schiefgeht.
     */
    default void send(final DiscordWebHookMessage message, final String uri) throws IOException
    {
        send(message, URI.create(uri));
    }

    /**
     * URL für den WebHook: Servereinstellungen -> Integrationen -> WebHooks anzeigen -> WebHook-URL kopieren
     *
     * @param message {@link DiscordWebHookMessage}
     * @param webHookId String
     * @param webHookToken String
     *
     * @throws IOException Falls was schiefgeht.
     */
    default void send(final DiscordWebHookMessage message, final String webHookId, final String webHookToken) throws IOException
    {
        send(message, String.format("https://discord.com/api/webhooks/%s/%s", webHookId, webHookToken));
    }

    /**
     * URL für den WebHook: Servereinstellungen -> Integrationen -> WebHooks anzeigen -> WebHook-URL kopieren
     *
     * @param message {@link DiscordWebHookMessage}
     * @param uri {@link URI}
     *
     * @throws IOException Falls was schiefgeht.
     */
    void send(final DiscordWebHookMessage message, final URI uri) throws IOException;

    /**
     * @param message {@link DiscordWebHookMessage}
     *
     * @return String
     *
     * @throws JacksonException Falls was schiefgeht.
     */
    default String toJson(final DiscordWebHookMessage message) throws JacksonException
    {
        ObjectMapper mapper = new ObjectMapper(); // .enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Keine Nulls ausgeben / serialisieren

        return mapper.writer().writeValueAsString(message);
    }

}
