package org.openhab.binding.dsaudio.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.FormContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.Fields;
import org.eclipse.smarthome.core.library.types.RawType;
import org.openhab.binding.dsaudio.internal.synology.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NonNullByDefault
public class DSAudio {
    private final Logger logger = LoggerFactory.getLogger(DSAudio.class);

    private final HttpClient httpClient;
    private final Gson gson;


    public DSAudio(HttpClient httpClient) {
        this.httpClient = httpClient;
        gson = new GsonBuilder().create();
    }

    public void startPlaying(DSAudioConfiguration configuration) {
        try {
            if (isSynologyActive(configuration) && login(configuration)) {
                logger.info("Synology Diskstation is active");

                Optional<Playlist> playlist = getPlaylists(configuration).stream().filter(playlist1 -> playlist1.getName().equals(configuration.getPlaylistName())).findFirst();
                getRemotePlayers(configuration).stream().filter(player1 -> player1.getName().equals(configuration.getPlayerName()))
                        .findFirst()
                        .ifPresent(player -> startRemotePlaying(configuration, player, playlist));
            } else {
                logger.info("Synology Diskstation is inActive or user is not logged in");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying(DSAudioConfiguration configuration) {
        try {
            if (isSynologyActive(configuration)) {
                logger.info("Synology Diskstation is active");
                login(configuration);
                getRemotePlayers(configuration).stream().filter(player1 -> player1.getName().equals(configuration.getPlayerName()))
                        .findFirst()
                        .ifPresent(player -> stopRemotePlaying(configuration, player));
            } else {
                logger.info("Synology Diskstation is inActive");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void next(DSAudioConfiguration configuration) {
        try {
            if (isSynologyActive(configuration)) {
                logger.info("Synology Diskstation is active");
                login(configuration);
                getRemotePlayers(configuration).stream().filter(player1 -> player1.getName().equals(configuration.getPlayerName()))
                        .findFirst()
                        .ifPresent(player -> playNextTrack(configuration, player));
            } else {
                logger.info("Synology Diskstation is inActive");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void previous(DSAudioConfiguration configuration) {
        try {
            if (isSynologyActive(configuration)) {
                logger.info("Synology Diskstation is active");
                login(configuration);
                getRemotePlayers(configuration).stream().filter(player1 -> player1.getName().equals(configuration.getPlayerName()))
                        .findFirst()
                        .ifPresent(player -> playPreviousTrack(configuration, player));
            } else {
                logger.info("Synology Diskstation is inActive");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<RawType> getCoverArt(DSAudioConfiguration configuration) {
        try {
            if (isSynologyActive(configuration)) {
                logger.info("Synology Diskstation is active");
                login(configuration);
                return getRemotePlayers(configuration).stream().filter(player1 -> player1.getName().equals(configuration.getPlayerName()))
                        .findFirst()
                        .flatMap(player -> getCoverArt(configuration, player));
            } else {
                logger.info("Synology Diskstation is inActive");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<RawType> getCoverArt(DSAudioConfiguration configuration, Player player) {
        try {
            RemotePlayerStatus remotePlayerStatus = getRemotePlayerStatus(configuration, player);

            String request = getBaseUrl(configuration) + "/webapi/AudioStation/cover.cgi";
            ContentResponse response = httpClient.newRequest(request).method(HttpMethod.GET)
                    .param("api", "SYNO.AudioStation.Cover")
                    .param("output_default", "true")
                    .param("is_hr", "false")
                    .param("library", "shared")
                    .param("method", "getsongcover")
                    .param("id", remotePlayerStatus.getSong().getId())
                    .param("version", "3")
                    .param("view", "playing")
                    .send();
            return Optional.of(new RawType(response.getContent(), response.getMediaType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();

    }

    private void startRemotePlaying(DSAudioConfiguration configuration, Player player, Optional<Playlist> playlist) {
        playlist.ifPresent(pl -> {
            try {
                RemotePlayerStatus remotePlayerStatus = getRemotePlayerStatus(configuration, player);

                String request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player.cgi";
                Fields fields = new Fields();
                fields.add("api", "SYNO.AudioStation.RemotePlayer");
                fields.add("method", "updateplaylist");
                fields.add("library", "shared");
                fields.add("offset", "0");
                fields.add("limit", Integer.toString(remotePlayerStatus.getPlaylist_total()));
                fields.add("play", "true");
                fields.add("version", "3");
                fields.add("id", player.getId());
                fields.add("keep_shuffle_order", "false");
                Container container = new Container("playlist", pl.getId());
                String containers = gson.toJson(Arrays.asList(container));
                fields.add("containers_json", containers);

                FormContentProvider formContentProvider = new FormContentProvider(fields);
                String response = httpClient.newRequest(request).method(HttpMethod.POST)
                        .content(formContentProvider)
                        .send().getContentAsString();
                logger.info("Update of playlist : " + response);
                request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player.cgi";
                httpClient.newRequest(request).method(HttpMethod.GET)
                        .param("api", "SYNO.AudioStation.RemotePlayer")
                        .param("method", "control")
                        .param("action", "next")
                        .param("id", player.getId())
                        .param("version", "3")
                        .send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private RemotePlayerStatus getRemotePlayerStatus(DSAudioConfiguration configuration, Player player) throws Exception {
        String request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player_status.cgi";
        ContentResponse response = httpClient.newRequest(request).method(HttpMethod.GET)
                .param("api", "SYNO.AudioStation.RemotePlayerStatus")
                .param("method", "getstatus")
                .param("id", player.getId())
                .param("version", "1")
                .param("additional", "song_tag")
                .send();
        Type collectionType = new TypeToken<Result<RemotePlayerStatus>>(){}.getType();
        return ((Result<RemotePlayerStatus>) gson.fromJson(response.getContentAsString(), collectionType)).getData();
    }

    private void stopRemotePlaying(DSAudioConfiguration configuration, Player player)  {
        try {
            String request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player.cgi";
            httpClient.newRequest(request).method(HttpMethod.GET)
                    .param("api", "SYNO.AudioStation.RemotePlayer")
                    .param("method", "control")
                    .param("action", "stop")
                    .param("id", player.getId())
                    .param("version", "3")
                    .send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playNextTrack(DSAudioConfiguration configuration, Player player)  {
        try {
            String request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player.cgi";
            httpClient.newRequest(request).method(HttpMethod.GET)
                    .param("api", "SYNO.AudioStation.RemotePlayer")
                    .param("method", "control")
                    .param("action", "next")
                    .param("id", player.getId())
                    .param("version", "3")
                    .send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playPreviousTrack(DSAudioConfiguration configuration, Player player)  {
        try {
            String request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player.cgi";
            httpClient.newRequest(request).method(HttpMethod.GET)
                    .param("api", "SYNO.AudioStation.RemotePlayer")
                    .param("method", "control")
                    .param("action", "prev")
                    .param("id", player.getId())
                    .param("version", "3")
                    .send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Player> getRemotePlayers(DSAudioConfiguration configuration) throws Exception {
        String request = getBaseUrl(configuration) + "/webapi/AudioStation/remote_player.cgi";
        ContentResponse response = httpClient.newRequest(request).method(HttpMethod.GET)
                .param("api", "SYNO.AudioStation.RemotePlayer")
                .param("version", "3")
                .param("method", "list")
                .send();

        Type collectionType = new TypeToken<Result<PlayerData>>(){}.getType();
        return ((Result<PlayerData>) gson.fromJson(response.getContentAsString(), collectionType)).getData().getPlayers();
    }

    private List<Playlist> getPlaylists(DSAudioConfiguration configuration) throws Exception {
        String request = getBaseUrl(configuration) + "/webapi/AudioStation/playlist.cgi";
        ContentResponse response = httpClient.newRequest(request).method(HttpMethod.GET)
                .param("api", "SYNO.AudioStation.Playlist")
                .param("version", "3")
                .param("method", "list")
                .send();
        Type collectionType = new TypeToken<Result<PlaylistData>>(){}.getType();
        return ((Result<PlaylistData>) gson.fromJson(response.getContentAsString(), collectionType)).getData().getPlaylists();
    }

    private boolean login(DSAudioConfiguration configuration) throws Exception {
        String request = getBaseUrl(configuration) + "/webapi/auth.cgi";
        ContentResponse response = httpClient.newRequest(request).method(HttpMethod.GET)
              .param("api", "SYNO.API.Auth")
              .param("version", "3")
              .param("method", "login")
              .param("session", "AudioStation")
              .param("format", "cookie")
              .param("account", configuration.getUsername())
              .param("passwd", configuration.getPassword())
              .send();

        APIInfo apiInfo = gson.fromJson(response.getContentAsString(), APIInfo.class);
        return apiInfo.isSuccess();

    }

    private boolean isSynologyActive(DSAudioConfiguration configuration) throws Exception {
        String request = getBaseUrl(configuration) + "/webapi/query.cgi";
        ContentResponse response = httpClient.newRequest(request).method(HttpMethod.GET)
                .param("api", "SYNO.API.Info")
                .param("version", "1")
                .param("method", "query")
                .param("query", "all")
                .send();

        APIInfo apiInfo = gson.fromJson(response.getContentAsString(), APIInfo.class);
        return apiInfo.isSuccess();
    }

    private String getBaseUrl(DSAudioConfiguration configuration) {
        return new Origin(configuration.isUseSSL() ? "https" : "http", configuration.getHost(), configuration.getPort()).asString();
    }
}


