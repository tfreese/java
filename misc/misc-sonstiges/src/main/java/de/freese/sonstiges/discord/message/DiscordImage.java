// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Großes Bild
 *
 * @author Thomas Freese
 */
@JsonRootName(value = "image")
public class DiscordImage
{
    /**
    *
    */
    private final String url;

    /**
     * Erstellt ein neues {@link DiscordImage} Object.
     *
     * @param url String
     */
    public DiscordImage(final String url)
    {
        super();

        this.url = url;
    }

    /**
     * @return String
     */
    public String getUrl()
    {
        return this.url;
    }
}
