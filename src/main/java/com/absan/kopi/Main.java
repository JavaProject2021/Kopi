package com.absan.kopi;


import com.absan.kopi.Spotify.SpotifyToken;
import com.absan.kopi.utils.SpotifyState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main extends Application {

    public static FXMLLoader fxmlLoader;
    public static SpotifyToken tokenGetter;

    @Override
    public void start(Stage stage) throws IOException {

        tokenGetter = new SpotifyToken();
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                tokenGetter.get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 3600, TimeUnit.SECONDS);


        if (!SpotifyState.isSpotifyOpen()) {
            fxmlLoader = new FXMLLoader(Main.class.getResource("LandingView.fxml"));
        } else {
            fxmlLoader = new FXMLLoader(Main.class.getResource("SecondView.fxml"));
        }
        Scene scene = new Scene(fxmlLoader.load(), 963, 593);
        stage.setResizable(false);
        stage.setTitle("Kopi v0.0.1");

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
