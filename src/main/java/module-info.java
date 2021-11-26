module com.absan.kopi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires org.jsoup;
    requires com.google.gson;
    requires ffmpeg;
    requires javafx.swing;


    opens com.absan.kopi to javafx.fxml;
    opens com.absan.kopi.utils to javafx.fxml;

    exports com.absan.kopi;
    opens com.absan.kopi.Spotify to javafx.fxml;
}