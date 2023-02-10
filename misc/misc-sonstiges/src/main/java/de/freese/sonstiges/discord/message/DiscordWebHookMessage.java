// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder({"tts", "avatar_url", "username", "content"})
public class DiscordWebHookMessage {
    private final List<DiscordEmbed> embeds = new ArrayList<>();
    private String avatarContent;
    private String avatarUrl;
    private String avatarUsername;
    /**
     * Text-To-Speech
     */
    private boolean tts;

    public DiscordWebHookMessage addEmbed(final DiscordEmbed embed) {
        this.embeds.add(embed);

        return this;
    }

    @JsonGetter("content")
    public String getAvatarContent() {
        return this.avatarContent;
    }

    @JsonGetter("avatar_url")
    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    @JsonGetter("username")
    public String getAvatarUsername() {
        return this.avatarUsername;
    }

    public List<DiscordEmbed> getEmbeds() {
        return this.embeds;
    }

    /**
     * Text-To-Speech
     */
    public boolean isTts() {
        return this.tts;
    }

    public DiscordWebHookMessage setAvatarContent(final String avatarContent) {
        this.avatarContent = avatarContent;

        return this;
    }

    public DiscordWebHookMessage setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;

        return this;
    }

    public DiscordWebHookMessage setAvatarUsername(final String avatarUsername) {
        this.avatarUsername = avatarUsername;

        return this;
    }

    /**
     * Text-To-Speech
     */
    public DiscordWebHookMessage setTts(final boolean tts) {
        this.tts = tts;

        return this;
    }
}
