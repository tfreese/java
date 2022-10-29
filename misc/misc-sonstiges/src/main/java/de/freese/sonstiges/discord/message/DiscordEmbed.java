// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder(
{
        "color", "author", "title", "titleDescription", "url", "thumbnail", "image", "fields", "footer"
})
@JsonRootName(value = "embed")
public class DiscordEmbed
{
    /**
     *
     */
    private DiscordAuthor author;
    /**
    *
    */
    private Color color;
    /**
    *
    */
    private final List<DiscordField> fields = new ArrayList<>();
    /**
    *
    */
    private DiscordFooter footer;
    /**
     * Großes Bild
     */
    private DiscordImage image;
    /**
     * Kleines Bild oben rechts
     */
    private DiscordThumbnail thumbnail;
    /**
     *
     */
    private String title;
    /**
     *
     */
    private String titleDescription;
    /**
     *
     */
    private String titleUrl;

    /**
     * @param field {@link DiscordField}
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed addField(final DiscordField field)
    {
        this.fields.add(field);

        return this;
    }

    /**
     * @return {@link DiscordAuthor}
     */
    public DiscordAuthor getAuthor()
    {
        return this.author;
    }

    /**
     * @return {@link Color}
     */
    @JsonSerialize(using = DiscordColorSerializer.class)
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @return List<Field>
     */
    public List<DiscordField> getFields()
    {
        return this.fields;
    }

    /**
     * @return {@link DiscordFooter}
     */
    public DiscordFooter getFooter()
    {
        return this.footer;
    }

    /**
     * Großes Bild
     *
     * @return {@link DiscordImage}
     */
    public DiscordImage getImage()
    {
        return this.image;
    }

    /**
     * Kleines Bild oben rechts
     *
     * @return {@link DiscordThumbnail}
     */
    public DiscordThumbnail getThumbnail()
    {
        return this.thumbnail;
    }

    /**
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @return String
     */
    @JsonGetter("description")
    public String getTitleDescription()
    {
        return this.titleDescription;
    }

    /**
     * @return String
     */
    @JsonGetter("url")
    public String getTitleUrl()
    {
        return this.titleUrl;
    }

    /**
     * @param author {@link DiscordAuthor}
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setAuthor(final DiscordAuthor author)
    {
        this.author = author;

        return this;
    }

    /**
     * @param color {@link Color}
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setColor(final Color color)
    {
        this.color = color;

        return this;
    }

    /**
     * @param footer {@link DiscordFooter}
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setFooter(final DiscordFooter footer)
    {
        this.footer = footer;

        return this;
    }

    /**
     * Großes Bild
     *
     * @param image {@link DiscordImage}
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setImage(final DiscordImage image)
    {
        this.image = image;

        return this;
    }

    /**
     * Kleines Bild oben rechts
     *
     * @param thumbnail {@link DiscordThumbnail}
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setThumbnail(final DiscordThumbnail thumbnail)
    {
        this.thumbnail = thumbnail;

        return this;
    }

    /**
     * @param title String
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setTitle(final String title)
    {
        this.title = title;

        return this;
    }

    /**
     * @param titleDescription String
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setTitleDescription(final String titleDescription)

    {
        this.titleDescription = titleDescription;

        return this;
    }

    /**
     * @param titleUrl String
     *
     * @return {@link DiscordEmbed}
     */
    public DiscordEmbed setTitleUrl(final String titleUrl)
    {
        this.titleUrl = titleUrl;

        return this;
    }
}
