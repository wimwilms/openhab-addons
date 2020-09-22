package org.openhab.binding.dsaudio.internal.synology;

public class RemotePlayerStatus {
    private int playlist_total;
    private Song song;

    public int getPlaylist_total() {
        return playlist_total;
    }

    public Song getSong() {
        return song;
    }
}
