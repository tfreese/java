// Created: 23.12.2020
package de.freese.jconky.model;

import java.net.URI;

/**
 * @author Thomas Freese
 */
public class MusicInfo {
    private final String album;
    private final String artist;
    private final int bitRate;
    private final URI imageUri;
    private final int length;
    private final int position;
    private final String title;

    public MusicInfo() {
        this(null, null, null, 0, 0, 0, null);
    }

    public MusicInfo(final String artist, final String album, final String title, final int length, final int position, final int bitRate, final URI imageUri) {
        super();

        this.artist = artist;
        this.album = album;
        this.title = title;
        this.length = length;
        this.position = position;
        this.bitRate = bitRate;
        this.imageUri = imageUri;
    }

    public String getAlbum() {
        return this.album;
    }

    public String getArtist() {
        return this.artist;
    }

    public int getBitRate() {
        return this.bitRate;
    }

    public URI getImageUri() {
        return this.imageUri;
    }

    public int getLength() {
        return this.length;
    }

    public int getPosition() {
        return this.position;
    }

    /**
     * Liefert den Fortschritt von 0 bis 1.<br>
     */
    public double getProgress() {
        if (getPosition() == 0) {
            return 0;
        }

        return (double) getPosition() / getLength();
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
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
