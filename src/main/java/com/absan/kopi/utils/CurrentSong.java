package com.absan.kopi.utils;

import com.absan.kopi.Google.GoogleLyrics;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class CurrentSong {
    @FXML
    Label SongName;
    @FXML
    Label LyricsLabel;

    public static StringProperty songname = new SimpleStringProperty();
    public static StringProperty lyricContent = new SimpleStringProperty();
    public static GoogleLyrics gLyrics = null;

    public static String currentSong = "";
    boolean init = false;


    public CurrentSong() {
        final String[] oldSong = {""};

        currentSong = SpotifyState.getCurrentSongName();
        Thread songChecker = new Thread(() -> {
            while (true) {

                if (!oldSong[0].equals(SpotifyState.getCurrentSongName())) {
                    oldSong[0] = SpotifyState.getCurrentSongName();
                    currentSong = SpotifyState.getCurrentSongName();
                    if (gLyrics == null && !currentSong.equals("Spotify not opened") && !currentSong.equals("Spotify Free")) {
                        gLyrics = new GoogleLyrics();
                    }
                    try {
                        callback();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        songChecker.start();

    }

    public void callback() throws IOException {

        System.out.println(currentSong);

        if (!currentSong.equals("Advertisement") && !currentSong.equals("Spotify not opened") && !currentSong.equals("Spotify Free")) {
            Platform.runLater(() -> {
                songname.set(CurrentSong.currentSong);
                SongName.textProperty().bind(songname);
            });
            Platform.runLater(() -> {
                try {
                    gLyrics.refreshLyrics();
                    lyricContent.set(gLyrics.finalLyrics);
                } catch (IOException e) {
                    e.printStackTrace();
                    lyricContent.set(String.valueOf(e));
                }
                LyricsLabel.textProperty().bind(lyricContent);
            });
            SpotifyState.getSongId();
        }
//        }
    }


}
