package com.absan.kopi;


import com.absan.kopi.Spotify.SpotifyToken;
import com.absan.kopi.utils.CurrentSong;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main extends Application {

    public static FXMLLoader fxmlLoader;
    public static SpotifyToken tokenGetter;
    public static Scene scene;
    public static Stage stage;

    @Override
    public void start(Stage stage) throws IOException, AWTException {

        tokenGetter = new SpotifyToken();
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


        fxmlLoader = new FXMLLoader(getClass().getResource("SecondView.fxml"));

        scene = new Scene(fxmlLoader.load(), 963, 593);
        Main.stage = stage;
        stage.setResizable(false);
        stage.setTitle("Kopi v0.0.1");
        Image icon = new Image(String.valueOf(getClass().getResource("Images/kopiLogo.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();

        new CurrentSong();

    }


    public static void main(String[] args) {
        launch();
    }
}
