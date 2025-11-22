// Created: 07.02.2021
package de.freese.sonstiges.discord;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import de.freese.sonstiges.discord.message.DiscordWebHookMessage;

/**
 * Sends a Message to a Discord-WebHook.<br>
 * URL for the WebHook: Servereinstellungen -> Integrationen -> WebHooks anzeigen -> WebHook-URL kopieren
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface DiscordWebHookSender {
    default void send(final DiscordWebHookMessage message, final String uri) throws IOException {
        send(message, URI.create(uri));
    }

    default void send(final DiscordWebHookMessage message, final String webHookId, final String webHookToken) throws IOException {
        send(message, String.format("https://discord.com/api/webhooks/%s/%s", webHookId, webHookToken));
    }

    void send(DiscordWebHookMessage message, URI uri) throws IOException;

    default String toJson(final DiscordWebHookMessage message) throws JacksonException {
        final JsonMapper jsonMapper = JsonMapper.builder()
                // .configure(SerializationFeature.INDENT_OUTPUT)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL)) // Keine Nulls ausgeben / serialisieren
                .build();

        return jsonMapper.writer().writeValueAsString(message);
    }
}
