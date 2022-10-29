// Created: 23.12.2020
package de.freese.jconky.model;

import java.net.URI;

/**
 * @author Thomas Freese
 */
public class MusicInfo
{
    /**
     *
     */
    private final String album;
    /**
     *
     */
    private final String artist;
    /**
     *
     */
    private final int bitRate;
    /**
     *
     */
    private final URI imageUri;
    /**
     *
     */
    private final int length;
    /**
     *
     */
    private final int position;
    /**
     *
     */
    private final String title;

    /**
     * Erstellt ein neues {@link MusicInfo} Object.
     */
    public MusicInfo()
    {
        this(null, null, null, 0, 0, 0, null);
    }

    /**
     * Erstellt ein neues {@link MusicInfo} Object.
     *
     * @param artist String
     * @param album String
     * @param title String
     * @param length int
     * @param position int
     * @param bitRate int
     * @param imageUri {@link URI}
     */
    public MusicInfo(final String artist, final String album, final String title, final int length, final int position, final int bitRate, final URI imageUri)
    {
        super();

        this.artist = artist;
        this.album = album;
        this.title = title;
        this.length = length;
        this.position = position;
        this.bitRate = bitRate;
        this.imageUri = imageUri;
    }

    /**
     * @return String
     */
    public String getAlbum()
    {
        return this.album;
    }

    /**
     * @return String
     */
    public String getArtist()
    {
        return this.artist;
    }

    /**
     * @return int
     */
    public int getBitRate()
    {
        return this.bitRate;
    }

    /**
     * @return {@link URI}
     */
    public URI getImageUri()
    {
        return this.imageUri;
    }

    /**
     * @return int
     */
    public int getLength()
    {
        return this.length;
    }

    /**
     * @return int
     */
    public int getPosition()
    {
        return this.position;
    }

    /**
     * Liefert den Fortschritt von 0 bis 1.<br>
     *
     * @return double
     */
    public double getProgress()
    {
        if (getPosition() == 0)
        {
            return 0;
        }

        return (double) getPosition() / getLength();
    }

    /**
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("artist=").append(this.artist);
        builder.append(", album=").append(this.album);
        builder.append(", title=").append(this.title);
        builder.append(", length=").append(this.length);
        builder.append(", position=").append(this.position);
        builder.append(", bitRate=").append(this.bitRate);
        builder.append(", imageUri=").append(this.imageUri);
        builder.append("]");

        return builder.toString();
    }
}
