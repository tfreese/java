// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder({"name", "value", "inline"})
@JsonRootName(value = "field")
public class DiscordField {
    private boolean inline;
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isInline() {
        return inline;
    }

    public DiscordField setInline(final boolean inline) {
        this.inline = inline;

        return this;
    }

    public DiscordField setName(final String name) {
        this.name = name;

        return this;
    }

    public DiscordField setValue(final String value) {
        this.value = value;

        return this;
    }
}
