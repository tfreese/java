package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Kleines Bild oben rechts.
 *
 * @author Thomas Freese
 * @since 06.07.2021
 */
@JsonRootName(value = "thumbnail")
public record DiscordThumbnail(String url) {
}
