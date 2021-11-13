package com.absan.kopi;

import com.absan.kopi.utils.CurrentSong;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

import static com.absan.kopi.Main.fxmlLoader;
import static com.absan.kopi.Main.scene;

public class SecondViewController {

    @FXML
    protected void switchToBigWindow() {
        Platform.runLater(() -> {

            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("Images/kopiLogo.png"))));
            stage.setTitle("Kopi v0.0.1");
            fxmlLoader = new FXMLLoader(Main.class.getResource("SecondView.fxml"));
            scene.getWindow().hide();
            try {
                scene = new Scene(fxmlLoader.load(), 963, 593);
            } catch (IOException e) {
                e.printStackTrace();
            }

            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        });
    }

}
