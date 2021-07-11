package org.openhab.binding.dsaudio.internal.synology;

import org.eclipse.jdt.annotation.Nullable;

public class Playlist {
    @Nullable
    private String id;
    @Nullable
    private String library;
    @Nullable
    private String name;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
