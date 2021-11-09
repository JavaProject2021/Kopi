package com.absan.kopi.utils;

import com.absan.kopi.Google.GoogleLyrics;
import com.absan.kopi.Musixmatch.MusixmatchLyrics;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Map;

import static com.absan.kopi.Main.fxmlLoader;
import static com.absan.kopi.SceneController.openSecondView;

public class CurrentSong {

    public static StringProperty songname = new SimpleStringProperty();
    public static StringProperty lyricContent = new SimpleStringProperty();
    public static GoogleLyrics gLyrics = null;
    public static MusixmatchLyrics mLyrics = null;
    public static boolean isInit = false;
    public static String currentSong = "";

    public static Map<String, Object> nameSpace = fxmlLoader.getNamespace();
    public static Label SongName = (Label) nameSpace.get("SongNameLabel");
    public static Label LyricsLabel = (Label) nameSpace.get("LyricsLabel");

    public CurrentSong() {
        final String[] oldSong = {""};


        currentSong = SpotifyState.getCurrentSongName();
        Thread songChecker = new Thread(() -> {
            while (true) {
//                System.out.println(nameSpace);
//                if (SongName == null) {
//                    nameSpace = fxmlLoader.getNamespace();
//                    SongName = (Label) nameSpace.get("SongNameLabel");
//                }
//
//                if (LyricsLabel == null) {
//                    nameSpace = fxmlLoader.getNamespace();
//                    LyricsLabel = (Label) nameSpace.get("LyricsLabel");SongName = (Label) nameSpace.get("SongNameLabel");
//                }
                if (!oldSong[0].equals(SpotifyState.getCurrentSongName())) {
                    oldSong[0] = SpotifyState.getCurrentSongName();
                    currentSong = SpotifyState.getCurrentSongName();
                    if (mLyrics == null && !currentSong.equals("Spotify not opened") && !currentSong.equals("Spotify Free")) {
                        mLyrics = new MusixmatchLyrics();
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

    @FXML
    public static void callback() throws IOException {
        isInit = true;
        System.out.println(currentSong);


        if (currentSong.equals("Spotify") || currentSong.equals("Spotify Free")) {
            Platform.runLater(() -> {
                try {
                    openSecondView();
                    nameSpace = fxmlLoader.getNamespace();
                    LyricsLabel = (Label) nameSpace.get("LyricsLabel");
                    SongName = (Label) nameSpace.get("SongNameLabel");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }

        if (!currentSong.equals("Advertisement") &&
                !currentSong.equals("Spotify not opened") &&
                !currentSong.equals("Spotify Free") &&
                !currentSong.equals("Spotify")) {

            SpotifyState.getSongId();
            Platform.runLater(() -> {
                try {
                    mLyrics.refreshLyrics();
                    lyricContent.set(MusixmatchLyrics.mfinalLyrics);
                } catch (IOException e) {
                    e.printStackTrace();
                    lyricContent.set(String.valueOf(e));
                }
                songname.set(CurrentSong.currentSong);
                if (SongName != null) {
                    SongName.textProperty().bind(songname);
                }

                if (LyricsLabel != null) {
                    LyricsLabel.textProperty().bind(lyricContent);
                }

            });
//            new DominantColor(SpotifyState.imageLink);

        }
    }


}
