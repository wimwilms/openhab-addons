package org.openhab.binding.dsaudio.internal.synology;

import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class PlaylistData {
    @Nullable
    private List<Playlist> playlists;

    public List<Playlist> getPlaylists() {
        return playlists;
    }
}
