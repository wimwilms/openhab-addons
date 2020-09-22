/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.dsaudio.internal;

/**
 * The {@link DSAudioConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Wim Wilms - Initial contribution
 */
public class DSAudioConfiguration {
    private String host;
    private Integer port;
    private boolean useSSL;
    private String username;
    private String password;
    private String playlistName;
    private String playerName;

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
