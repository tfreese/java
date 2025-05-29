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
@JsonPropertyOrder({"color", "author", "title", "titleDescription", "url", "thumbnail", "image", "fields", "footer"})
@JsonRootName(value = "embed")
public class DiscordEmbed {
    private final List<DiscordField> fields = new ArrayList<>();

    private DiscordAuthor author;
    private Color color;
    private DiscordFooter footer;
    /**
     * Big Picture.
     */
    private DiscordImage image;
    /**
     * Little Picture upper right.
     */
    private DiscordThumbnail thumbnail;
    private String title;
    private String titleDescription;
    private String titleUrl;

    public DiscordEmbed addField(final DiscordField field) {
        this.fields.add(field);

        return this;
    }

    public DiscordAuthor getAuthor() {
        return author;
    }

    @JsonSerialize(using = DiscordColorSerializer.class)
    public Color getColor() {
        return color;
    }

    public List<DiscordField> getFields() {
        return fields;
    }

    public DiscordFooter getFooter() {
        return footer;
    }

    /**
     * Big Picture.
     */
    public DiscordImage getImage() {
        return image;
    }

    /**
     * Little Picture upper right.
     */
    public DiscordThumbnail getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    @JsonGetter("description")
    public String getTitleDescription() {
        return titleDescription;
    }

    @JsonGetter("url")
    public String getTitleUrl() {
        return titleUrl;
    }

    public DiscordEmbed setAuthor(final DiscordAuthor author) {
        this.author = author;

        return this;
    }

    public DiscordEmbed setColor(final Color color) {
        this.color = color;

        return this;
    }

    public DiscordEmbed setFooter(final DiscordFooter footer) {
        this.footer = footer;

        return this;
    }

    /**
     * Big Picture.
     */
    public DiscordEmbed setImage(final DiscordImage image) {
        this.image = image;

        return this;
    }

    /**
     * Little Picture upper right.
     */
    public DiscordEmbed setThumbnail(final DiscordThumbnail thumbnail) {
        this.thumbnail = thumbnail;

        return this;
    }

    public DiscordEmbed setTitle(final String title) {
        this.title = title;

        return this;
    }

    public DiscordEmbed setTitleDescription(final String titleDescription) {
        this.titleDescription = titleDescription;

        return this;
    }

    public DiscordEmbed setTitleUrl(final String titleUrl) {
        this.titleUrl = titleUrl;

        return this;
    }
}
