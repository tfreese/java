// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder(
{
        "name", "value", "inline"
})
@JsonRootName(value = "field")
public class DiscordField
{
    /**
    *
    */
    private boolean inline;
    /**
    *
    */
    private String name;
    /**
    *
    */
    private String value;

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return String
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * @return boolean
     */
    public boolean isInline()
    {
        return this.inline;
    }

    /**
     * @param inline boolean
     *
     * @return {@link DiscordField}
     */
    public DiscordField setInline(final boolean inline)
    {
        this.inline = inline;

        return this;
    }

    /**
     * @param name String
     *
     * @return {@link DiscordField}
     */
    public DiscordField setName(final String name)
    {
        this.name = name;

        return this;
    }

    /**
     * @param value String
     *
     * @return {@link DiscordField}
     */
    public DiscordField setValue(final String value)
    {
        this.value = value;

        return this;
    }
}
