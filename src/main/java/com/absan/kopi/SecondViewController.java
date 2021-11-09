package com.absan.kopi;

import javafx.fxml.FXML;

import java.awt.*;

public class SecondViewController {
    @FXML
    Label SongName;

    @FXML
    Label LyricsLabel;

    @FXML
    public void setOutput(String lyrics, String name) {
        SongName.setText(name);
        LyricsLabel.setText(lyrics);
    }


}
