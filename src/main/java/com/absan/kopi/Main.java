package com.absan.kopi;


import com.absan.kopi.Spotify.SpotifyToken;
import com.absan.kopi.utils.CurrentSong;
import com.absan.kopi.utils.SpotifyState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main extends Application {

    public static SpotifyToken tokenGetter;
    public static Scene scene1;
    public static Stage rootStage;
    public static FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        rootStage = stage;
        tokenGetter = new SpotifyToken();

//      Setup spotify Access token refresher for every 60 min
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        tokenGetter.getToken();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                tokenGetter.getToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 3600, TimeUnit.SECONDS);


        if (!SpotifyState.isSpotifyOpen()) {
            fxmlLoader = new FXMLLoader(Main.class.getResource("LandingView.fxml"));
        } else {
            fxmlLoader = new FXMLLoader(Main.class.getResource("SecondView.fxml"));
        }

        new CurrentSong();
        scene1 = new Scene(fxmlLoader.load());
        stage.setScene(scene1);
        stage.setResizable(false);
        stage.setTitle("Kopi v0.0.1");
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }
}
