package com.absan.kopi.Google;

import com.absan.kopi.utils.CurrentSong;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GoogleLyrics {

    private String artistName = CurrentSong.getCurrentSong().split(" - ")[0];
    private String songName = CurrentSong.getCurrentSong().split(" - ")[1];
    private ArrayList<String> googleLyrics = new ArrayList<String>();
    private String finalLyrics = "";

    public String getFinalLyrics() {
        return finalLyrics;
    }

    public void setFinalLyrics(String finalLyrics) {
        this.finalLyrics = finalLyrics;
    }

    String googleQuery = "https://www.google.com/search?q=" +
            URLEncoder.encode(songName, StandardCharsets.UTF_8) + "+" +
            URLEncoder.encode(artistName, StandardCharsets.UTF_8) +
            "+lyrics";

    final String userAgent =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";


    public void refreshLyrics() throws IOException {

        artistName = CurrentSong.getCurrentSong().split(" - ")[0];
        songName = CurrentSong.getCurrentSong().split(" - ")[1];
        googleLyrics = new ArrayList<>();
        finalLyrics = "";
        googleQuery = "https://www.google.com/search?q=" +
                URLEncoder.encode(songName, StandardCharsets.UTF_8) + "+" +
                URLEncoder.encode(artistName, StandardCharsets.UTF_8) +
                "+lyrics";

        Document doc = Jsoup.connect(googleQuery).userAgent(userAgent).timeout(60 * 1000).get();
        boolean hasLyrics = doc.select("span").select("[jsname='YS01Ge']").first() != null;
        if (hasLyrics) {
            Elements elements = doc.select("span").select("[jsname='YS01Ge']");
            elements.forEach((line) -> {
                String Currentline = String.valueOf(line).replace("<span jsname=\"YS01Ge\">", "").replace("</span>", "").strip();
                googleLyrics.add(Currentline);
            });
        }

        googleLyrics.remove(0);
        googleLyrics.remove(0);
        googleLyrics.remove(0);
        googleLyrics.remove(0);

        StringBuilder temp = new StringBuilder();
        googleLyrics.forEach(line -> {
            if (line.length() == 0) {
                temp.append(line).append("♪");
            } else {
                temp.append(line).append("\n");
            }
        });

        setFinalLyrics(String.valueOf(temp));

    }


}
