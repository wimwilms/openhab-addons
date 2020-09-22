package org.openhab.binding.dsaudio.internal.synology;

import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class PlayerData {
    @Nullable
    private List<Player> players;

    public List<Player> getPlayers() {
        return players;
    }
}
