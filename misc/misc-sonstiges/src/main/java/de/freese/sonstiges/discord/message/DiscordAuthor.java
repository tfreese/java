// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder(
        {
                "name", "nameUrl", "iconUrl"
        })
@JsonRootName(value = "author")
public class DiscordAuthor
{
    private String iconUrl;

    private String name;

    private String nameUrl;

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    public String getName()
    {
        return this.name;
    }

    @JsonGetter("url")
    public String getNameUrl()
    {
        return this.nameUrl;
    }

    public DiscordAuthor setIconUrl(final String iconUrl)
    {
        this.iconUrl = iconUrl;

        return this;
    }

    public DiscordAuthor setName(final String name)
    {
        this.name = name;

        return this;
    }

    public DiscordAuthor setNameUrl(final String nameUrl)
    {
        this.nameUrl = nameUrl;

        return this;
    }
}
