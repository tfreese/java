// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Kleines Bild oben rechts
 *
 * @author Thomas Freese
 */
@JsonRootName(value = "thumbnail")
public class DiscordThumbnail
{
    private final String url;

    public DiscordThumbnail(final String url)
    {
        super();

        this.url = url;
    }

    public String getUrl()
    {
        return this.url;
    }
}
