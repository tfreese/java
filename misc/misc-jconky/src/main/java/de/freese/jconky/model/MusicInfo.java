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
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public int getBitRate() {
        return bitRate;
    }

    public URI getImageUri() {
        return imageUri;
    }

    public int getLength() {
        return length;
    }

    public int getPosition() {
        return position;
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
        return title;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("artist=").append(artist);
        builder.append(", album=").append(album);
        builder.append(", title=").append(title);
        builder.append(", length=").append(length);
        builder.append(", position=").append(position);
        builder.append(", bitRate=").append(bitRate);
        builder.append(", imageUri=").append(imageUri);
        builder.append("]");

        return builder.toString();
    }
}
