// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder(
        {
                "text", "iconUrl"
        })
@JsonRootName(value = "footer")
public class DiscordFooter
{
    private String iconUrl;

    private String text;

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    public String getText()
    {
        return this.text;
    }

    public DiscordFooter setIconUrl(final String iconUrl)
    {
        this.iconUrl = iconUrl;

        return this;
    }

    public DiscordFooter setText(final String text)
    {
        this.text = text;

        return this;
    }
}
