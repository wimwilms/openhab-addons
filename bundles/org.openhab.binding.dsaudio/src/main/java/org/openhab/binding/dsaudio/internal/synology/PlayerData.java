package org.openhab.binding.dsaudio.internal.synology;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

public class PlayerData {
    @Nullable
    private List<Player> players;

    public List<Player> getPlayers() {
        return players;
    }
}
