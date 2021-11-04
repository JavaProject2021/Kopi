package com.absan.kopi;

import com.absan.kopi.Google.GoogleLyrics;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;


public class LandingViewController {

    @FXML
    Pane connectSpotify;
    @FXML
    ImageView spotifyInactive;
    @FXML
    ImageView spotifyActive;
    @FXML
    Label loginButtonText;

    @FXML
    public static AnchorPane rootPane;


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
    public static void openSecondView() throws IOException {
        Stage rootStage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(LandingViewController.class.getResource("SecondView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 963, 593);
        rootStage.setScene(scene);
    }

    @FXML
    protected void loginClickEffect() throws IOException {
        openSecondView();
    }


}
