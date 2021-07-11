package org.openhab.binding.dsaudio.internal.synology;

public class Song {
    private String id;
    private String title;
    private Additional additional;

    public Song() {
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.additional.getSong_tag().getArtist();
    }
}
