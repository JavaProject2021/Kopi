package com.absan.kopi.utils;

import com.absan.kopi.Google.GoogleLyrics;
import com.absan.kopi.Main;
import com.absan.kopi.Musixmatch.MusixmatchLyrics;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

import static com.absan.kopi.Main.*;

public class CurrentSong {
    @FXML
    Label SongName;
    @FXML
    Label ArtistName;
    @FXML
    Label LyricsLabel;
    @FXML
    Label spotifyStatus;
    @FXML
    Pane connectSpotify;
    @FXML
    ImageView spotifyInactive;
    @FXML
    ImageView spotifyActive;
    @FXML
    Label loginButtonText;
    @FXML
    Label Divider;
    @FXML
    Pane InitialOverlay;
    @FXML
    ImageView songState;
    @FXML
    ScrollPane scrollPane;


    public static StringProperty songname = new SimpleStringProperty();
    public static StringProperty artistname = new SimpleStringProperty();
    public static StringProperty lyricContent = new SimpleStringProperty();
    public static StringProperty spotifystatus = new SimpleStringProperty();
    public static GoogleLyrics gLyrics = null;
    public static MusixmatchLyrics mLyrics = null;

    public static boolean isStarted;

    public static String currentSong = "";


    public CurrentSong() throws IOException {
        if (!CurrentSong.isStarted) {
            StartChecking();
            isStarted = true;
        }
        Platform.runLater(() -> {
            if (InitialOverlay != null) {
                InitialOverlay.setStyle("visibility:hidden;");
            }
        });
        callback();


    }

    protected void StartChecking() {
        final String[] oldSong = {""};

        currentSong = SpotifyState.getCurrentSongName();
        Thread songChecker = new Thread(() -> {
            while (true) {

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

    public void callback() throws IOException {

        if (!currentSong.equals("Advertisement") && !currentSong.equals("Spotify not opened") && !currentSong.equals("Spotify Free") && !currentSong.equals("Spotify")) {
            Platform.runLater(() -> {
                spotifystatus.set("Playing...");
                playButton();
                try {
                    spotifyStatus.textProperty().bind(spotifystatus);
                } catch (Exception ignored) {
                }
                if (scrollPane != null) {
                    scrollPane.setVvalue(0.0);
                }
            });
            Platform.runLater(() -> {
                songname.set(CurrentSong.currentSong.split(" - ")[1]);
                try {
                    SongName.textProperty().bind(songname);
                } catch (Exception ignored) {
                }
            });
            Platform.runLater(() -> {
                artistname.set(CurrentSong.currentSong.split(" - ")[0]);
                try {
                    ArtistName.textProperty().bind(artistname);
                } catch (Exception ignored) {
                }
            });
            SpotifyState.getSongId();
            Platform.runLater(() -> {
                try {
                    mLyrics.refreshLyrics();
                    lyricContent.set(MusixmatchLyrics.mfinalLyrics);
                } catch (IOException e) {
                    e.printStackTrace();
                    lyricContent.set(String.valueOf(e));
                }
                try {
                    LyricsLabel.textProperty().bind(lyricContent);
                } catch (Exception ignored) {
                }

                try {
                    new DominantColor(SpotifyState.imageLink);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } else {
            if (currentSong.equals("Spotify Free") || currentSong.equals("Spotify")) {
                Platform.runLater(() -> {
                    spotifystatus.set("Not Playing...");
                    if(spotifyStatus != null) {
                        spotifyStatus.textProperty().bind(spotifystatus);
                    }
                });
            } else if (currentSong.equals("Advertisement")) {
                Platform.runLater(() -> {
                    spotifystatus.set("Advertisement");
                    spotifyStatus.textProperty().bind(spotifystatus);
                });
            } else {
                Platform.runLater(() -> {
                    spotifystatus.set("Spotify Not Open...");
                    spotifyStatus.textProperty().bind(spotifystatus);
                });
            }
        }
    }


    @FXML
    protected void loginHoverEnterEffect() {
        spotifyInactive.setVisible(false);
        spotifyActive.setOpacity(1.0);
        loginButtonText.setTextFill(Color.color(1, 1, 1));
    }

    @FXML
    protected void loginHoverExitEffect() {
        spotifyInactive.setVisible(true);
        spotifyActive.setOpacity(0);
        loginButtonText.setTextFill(Color.rgb(30, 215, 96));
    }

    @FXML
    protected void SmallScreenEnterEffect() {
        SongName.setVisible(true);
        ArtistName.setVisible(true);
        Divider.setVisible(true);
    }

    @FXML
    protected void SmallScreenExitEffect() {
        SongName.setVisible(false);
        ArtistName.setVisible(false);
        Divider.setVisible(false);
    }

    @FXML
    protected void playButton() {
        if(songState != null) {
            if (currentSong.equals("Spotify Free") || currentSong.equals("Spotify")) {
                songState.setImage(new Image(String.valueOf(Main.class.getResource("Images/play.png"))));
            } else {
                songState.setImage(new Image(String.valueOf(Main.class.getResource("Images/pause.png"))));
            }
        }
    }

    @FXML
    protected void switchToSmallWindow() throws IOException {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader(Main.class.getResource("SmallWindow.fxml"));
            try {
                scene = new Scene(fxmlLoader.load(), 490, 180);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
        });
        callback();

    }


    @FXML
    protected void switchToBigWindow() {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader(Main.class.getResource("SecondView.fxml"));
            try {
                scene = new Scene(fxmlLoader.load(), 963, 593);
            } catch (IOException e) {
                e.printStackTrace();
            }

            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(false);
        });
    }

}
