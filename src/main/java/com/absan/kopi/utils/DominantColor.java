package com.absan.kopi.utils;

import com.spikeify.ffmpeg.colorthief.ColorThief;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.absan.kopi.Main.scene;

public class DominantColor {

    String imageUrl;
    Image image;
    String dominantHex;
    String dominantForeGround;

    DominantColor(String imageURL) throws IOException {
        this.imageUrl = imageURL;
        getImage();
        getDominantColor();
        getForeground();
        setBGFG();
    }

    @FXML
    protected void setBGFG() {
        scene.lookup("#scrollPane").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\"; -fx-background-radius: 10");
        scene.lookup("#LyricsLabel").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\";");
        scene.lookup("#lyricsContainer").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\";");
        scene.lookup(".scroll-bar:vertical .thumb").setStyle("-fx-background-color: \"" + this.dominantForeGround + "\";");

        if(scene.lookup("#coverImage") == null) {
            scene.lookup("#SongDataContainer").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\";");
            scene.lookup("#SongName").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\";");
            scene.lookup("#ArtistName").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\";");
            scene.lookup("#Divider").setStyle("-fx-text-fill: \"" + this.dominantForeGround + "\";" + "-fx-background-color: \"" + this.dominantHex + "\";");
        }
    }

    protected void getImage() throws IOException {
        URL imgUrl = new URL(this.imageUrl);
        this.image = ImageIO.read(imgUrl);

        if(scene.lookup("#coverImage") != null) {
            ((ImageView) scene.lookup("#coverImage")).setImage(SwingFXUtils.toFXImage(toBufferedImage(this.image), null));
        }
    }

    protected void getDominantColor() {
        String results = ColorThief.getDominantHex(toBufferedImage(this.image));
        this.dominantHex = results;
    }

    protected static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    private float CalculateLuminance(ArrayList<Integer> rgb) {
//      luminance = 0.2126*R + 0.7152*G + 0.0722*B
        return (float) (0.2126 * rgb.get(0) + 0.7152 * rgb.get(1) + 0.0722 * rgb.get(2));
    }

    private ArrayList<Integer> HexToRBG(String colorStr) {
        ArrayList<Integer> rbg = new ArrayList<Integer>();
        rbg.add(Integer.valueOf(colorStr.substring(1, 3), 16));
        rbg.add(Integer.valueOf(colorStr.substring(3, 5), 16));
        rbg.add(Integer.valueOf(colorStr.substring(5, 7), 16));
        return rbg;
    }

    protected void getForeground() {
        float luminance = this.CalculateLuminance(this.HexToRBG(this.dominantHex));
        this.dominantForeGround = (luminance < 140) ? "#fff" : "#000";
    }


}
