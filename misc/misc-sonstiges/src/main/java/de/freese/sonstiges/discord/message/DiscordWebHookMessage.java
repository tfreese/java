// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder(
{
        "tts", "avatar_url", "username", "content"
})
public class DiscordWebHookMessage
{
    /**
    *
    */
    private String avatarContent;
    /**
    *
    */
    private String avatarUrl;
    /**
    *
    */
    private String avatarUsername;
    /**
    *
    */
    private final List<DiscordEmbed> embeds = new ArrayList<>();
    /**
     * Text-To-Speech
     */
    private boolean tts;

    /**
     * @param embed {@link DiscordEmbed}
     *
     * @return {@link DiscordWebHookMessage}
     */
    public DiscordWebHookMessage addEmbed(final DiscordEmbed embed)
    {
        this.embeds.add(embed);

        return this;
    }

    /**
     * @return String
     */
    @JsonGetter("content")
    public String getAvatarContent()
    {
        return this.avatarContent;
    }

    /**
     * @return String
     */
    @JsonGetter("avatar_url")
    public String getAvatarUrl()
    {
        return this.avatarUrl;
    }

    /**
     * @return String
     */
    @JsonGetter("username")
    public String getAvatarUsername()
    {
        return this.avatarUsername;
    }

    /**
     * @return List<Embed>
     */
    public List<DiscordEmbed> getEmbeds()
    {
        return this.embeds;
    }

    /**
     * Text-To-Speech
     *
     * @return boolean
     */
    public boolean isTts()
    {
        return this.tts;
    }

    /**
     * @param avatarContent String
     *
     * @return {@link DiscordWebHookMessage}
     */
    public DiscordWebHookMessage setAvatarContent(final String avatarContent)
    {
        this.avatarContent = avatarContent;

        return this;
    }

    /**
     * @param avatarUrl String
     *
     * @return {@link DiscordWebHookMessage}
     */
    public DiscordWebHookMessage setAvatarUrl(final String avatarUrl)
    {
        this.avatarUrl = avatarUrl;

        return this;
    }

    /**
     * @param avatarUsername String
     *
     * @return {@link DiscordWebHookMessage}
     */
    public DiscordWebHookMessage setAvatarUsername(final String avatarUsername)
    {
        this.avatarUsername = avatarUsername;

        return this;
    }

    /**
     * Text-To-Speech
     *
     * @param tts boolean
     *
     * @return {@link DiscordWebHookMessage}
     */
    public DiscordWebHookMessage setTts(final boolean tts)
    {
        this.tts = tts;

        return this;
    }
}
