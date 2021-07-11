package org.openhab.binding.dsaudio.internal.synology;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

public class PlaylistData {
    @Nullable
    private List<Playlist> playlists;

    public List<Playlist> getPlaylists() {
        return playlists;
    }
}
