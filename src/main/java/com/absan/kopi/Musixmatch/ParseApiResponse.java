package com.absan.kopi.Musixmatch;

import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseApiResponse {

    public String apiQuery = "";
    public String apiResponse = "";
    public int responseCode = 0;
    public static List<String> unsyncedLyrics = null;
    public static List<RichLyrics> syncedLyrics = null;

    final String userAgent =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";


    public ParseApiResponse(String ApiQuery) throws IOException {
        this.apiQuery = ApiQuery;
//        System.out.println(ApiQuery);
        getResponse();
        getResponseCode();
        getUnsyncedLyrics();
        getSyncedLyrics();
    }

    protected void getResponse() throws IOException {
        Document doc = Jsoup.connect(String.valueOf(this.apiQuery)).userAgent(userAgent).timeout(60 * 1000).get();
        this.apiResponse = doc.select("body").text();
    }

    protected void getResponseCode() {
        String lyricsStatusCode = "";
        lyricsStatusCode = String.valueOf(JsonParser.parseString(apiResponse).getAsJsonObject().getAsJsonObject("message"));
        lyricsStatusCode = String.valueOf(JsonParser.parseString(lyricsStatusCode).getAsJsonObject().getAsJsonObject("body"));
        lyricsStatusCode = String.valueOf(JsonParser.parseString(lyricsStatusCode).getAsJsonObject().getAsJsonObject("macro_calls"));
        lyricsStatusCode = String.valueOf(JsonParser.parseString(lyricsStatusCode).getAsJsonObject().getAsJsonObject("track.lyrics.get"));
        lyricsStatusCode = String.valueOf(JsonParser.parseString(lyricsStatusCode).getAsJsonObject().getAsJsonObject("message"));
        lyricsStatusCode = String.valueOf(JsonParser.parseString(lyricsStatusCode).getAsJsonObject().getAsJsonObject("header"));
        lyricsStatusCode = String.valueOf(JsonParser.parseString(lyricsStatusCode).getAsJsonObject().get("status_code"));
        this.responseCode = Integer.parseInt(lyricsStatusCode);
    }

    protected void getUnsyncedLyrics() {
        List<String> lyrics = new ArrayList<>();
        String lyricsString = "";
        String tempLyrics = "";

        lyricsString = String.valueOf(JsonParser.parseString(apiResponse).getAsJsonObject().getAsJsonObject("message"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("body"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("macro_calls"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("track.lyrics.get"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("message"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("body"));

        try {
            tempLyrics = lyricsString;
            tempLyrics = String.valueOf(JsonParser.parseString(tempLyrics).getAsJsonObject().getAsJsonObject("lyrics"));
            tempLyrics = String.valueOf(JsonParser.parseString(tempLyrics).getAsJsonObject().get("lyrics_body"));
            lyricsString = tempLyrics;
        } catch (Exception e) {
            try {
                tempLyrics = lyricsString;
                tempLyrics = String.valueOf(JsonParser.parseString(tempLyrics).getAsJsonObject().getAsJsonArray("crowd_lyrics_list"));
                tempLyrics = String.valueOf(JsonParser.parseString(tempLyrics).getAsJsonArray().get(0));
                tempLyrics = String.valueOf(JsonParser.parseString(tempLyrics).getAsJsonObject().getAsJsonObject("lyrics"));
                tempLyrics = String.valueOf(JsonParser.parseString(tempLyrics).getAsJsonObject().get("lyrics_body"));
                lyricsString = tempLyrics;
            } catch (Exception e2) {
                lyricsString = "No musixmatch";
            }
        }

        if (!lyricsString.equals("No musixmatch")) {
            lyricsString = lyricsString.substring(1, lyricsString.length() - 1);
            if (lyricsString.length() % 2 != 0) {
                lyricsString += " ";
            }
            StringBuilder modifiedLyrics = new StringBuilder();
            for (int i = 0; i < lyricsString.length() - 1; i++) {
                if (lyricsString.charAt(i) == '\\') {
                    if (lyricsString.charAt(i + 1) == 'n') {
                        modifiedLyrics.append("###");
                        i++;
                    }
                } else {
                    modifiedLyrics.append(lyricsString.charAt(i));
                }
            }
            lyricsString = String.valueOf(modifiedLyrics);
        }

        for (String line : lyricsString.split("###")) {
            if (line.length() != 0) {
                lyrics.add(line);
            } else {
                lyrics.add("");
                lyrics.add("♪");
                lyrics.add("");
            }

        }
        this.unsyncedLyrics = lyrics;
    }

    protected void getSyncedLyrics() {
        List<RichLyrics> lyrics = new ArrayList<>();
        String lyricsString = "";

        lyricsString = String.valueOf(JsonParser.parseString(apiResponse).getAsJsonObject().getAsJsonObject("message"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("body"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("macro_calls"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("track.subtitles.get"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("message"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonObject("body"));

        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().getAsJsonArray("subtitle_list"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonArray().get(0));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().get("subtitle"));
        lyricsString = String.valueOf(JsonParser.parseString(lyricsString).getAsJsonObject().get("subtitle_body"));
        lyricsString = lyricsString.substring(1, lyricsString.length() - 1);

        StringBuilder modifiedLyrics = new StringBuilder();
        for (int i = 0; i < lyricsString.length() - 1; i++) {
            if (lyricsString.charAt(i) == '\\') {
                if (lyricsString.charAt(i + 1) == '"') {
                    modifiedLyrics.append("###");
                    i++;
                }
            } else {
                modifiedLyrics.append(lyricsString.charAt(i));
            }
        }
        lyricsString = String.valueOf(modifiedLyrics);

        for (int i = 1; i < lyricsString.split("###text###:").length - 1; i++) {
            String currentLine = lyricsString.split("###text###:")[i].replace("}},{", "");
            String currentLyrics = currentLine.split("###,###time###:")[0].replaceAll("###", "");
            if (currentLyrics.length() == 0) {
                currentLyrics = "♪";
            }

            Double total = Double.parseDouble(currentLine.split("###total###:")[1].split(",###minutes###:")[0]);
            Double minutes = Double.parseDouble(currentLine.split(",###minutes###:")[1].split(",###seconds###:")[0]);
            Double seconds = Double.parseDouble(currentLine.split(",###seconds###:")[1].split(",###hundredths###:")[0]);
            Double hundredths = Double.parseDouble(currentLine.split(",###hundredths###:")[1]);
            lyrics.add(new RichLyrics(currentLyrics, total, minutes, seconds, hundredths));
        }
        this.syncedLyrics = lyrics;
    }

}

