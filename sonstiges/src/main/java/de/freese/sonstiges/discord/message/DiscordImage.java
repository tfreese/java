package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Großes Bild
 *
 * @author Thomas Freese
 * @since 06.07.2021
 */
@JsonRootName(value = "image")
public record DiscordImage(String url) {
}
