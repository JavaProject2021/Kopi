package com.absan.kopi.Musixmatch;

import com.absan.kopi.Google.GoogleLyrics;
import com.absan.kopi.utils.CurrentSong;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MusixmatchLyrics {
    private String artistName;
    private String songName;
    private List<String> musixmatchUnsyncedLyrics = new ArrayList<String>();
    private List<RichLyrics> musixmatchSyncedLyrics = new ArrayList<RichLyrics>();
    private static String mfinalLyrics = "";

    public static String getFinalLyrics() {
        return mfinalLyrics;
    }

    public void setFinalLyrics(String finalLyrics) {
        mfinalLyrics = finalLyrics;
    }

    List<String> userTokenList = new ArrayList(Arrays.asList(
            "2110231e1499a3fcd07aa419d7eee9f4e7d46e17650d65198585d5",
            "2110294e8f7c010b6079b37c23794900a9e0254fc6b0577bbca0d5",
            "211029c2afb01fd66b19acf3d980b02e23bba7fd3035d972f811ec",
            "21102911abab14b9e4b263adae4ad368dd21d02c35c3bd612e3338",
            "211029b9fda36043374e84dbe676f940142d3359f81f6b6004d389",
            "210112990dc32665215f58362e1388fb29fefcee84d1957a2a3da1",
            "210118c17557662a82c131cd471530683eafb1121f26c838fc7d3b",
            "21011884321ecea0403cb27153484af7384ed538c0d59ba4fcbe86",
            "2101185ee72d12fc9e10c69d0f2bbecc07a3ae09301b28c0dfc916",
            "210118843620ff2e670a04c841fb7aacd9d39f1c45e4cca1ae8588",
            "210118f1b42762804e58437dc9c880bb38d649366e93e65e5795d1",
            "21011894c4e64b0bbe4b3c7974208f8ca9171fc40fd8f39d389d41",
            "21011810aa17df4b66aa1e83439e2af7ae5d324cc20109f002c76a",
            "210118b1426b20b7a41bab5aae1a40a75a00fa972706ba4500e958",
            "21011885a5537210b803e2cde22710017848c392709ac9a8f8658e",
            "210526086118cea30b65e5a0379401520eea4a12869342ea91ae25",
            "2105265dfe2e577946119ac52928b056daa65506f2147174bbcd59"
    ));

    String userToken = userTokenList.get(new Random().nextInt(userTokenList.size()));


    public void refreshLyrics() throws IOException {
        artistName = CurrentSong.getCurrentSong().split(" - ")[0];
        songName = CurrentSong.getCurrentSong().split(" - ")[1];

        StringBuilder musixmatchQuery = new StringBuilder()
                .append("https://apic-desktop.musixmatch.com/ws/1.1/macro.subtitles.get?")
                .append("format=json&")
                .append("namespace=lyrics_synched&")
                .append("part=lyrics_crowd%2Cuser%2Clyrics_verified_by&")
                .append("q_artist=").append(URLEncoder.encode(artistName, StandardCharsets.UTF_8))
                .append("&q_track=").append(URLEncoder.encode(songName, StandardCharsets.UTF_8))
                .append("&user_language=en&")
                .append("tags=nowplaying&")
                .append("f_subtitle_length_max_deviation=1&")
                .append("subtitle_format=mxm&")
                .append("app_id=web-desktop-app-v1.0&")
                .append("usertoken=").append(userToken)
                .append("&guid=3733bd57-494d-4534-987f-c6a369302507&")
                .append("signature=0IwdyrdfMG0e%2B8B%2FhnuTnnKAhvI%3D&")
                .append("signature_protocol=sha1");

        ParseApiResponse parseApiResponse = new ParseApiResponse(String.valueOf(musixmatchQuery));

        musixmatchUnsyncedLyrics = parseApiResponse.getUnSyncedLyrics();
        musixmatchSyncedLyrics = parseApiResponse.getSyncedLyrics();


        StringBuilder sb = new StringBuilder();
        musixmatchUnsyncedLyrics.forEach(line -> {
            sb.append(line).append("\n");
        });

        if (String.valueOf(sb).length() == 0) {
            setFinalLyrics(new GoogleLyrics().getFinalLyrics());
        } else {
            setFinalLyrics(String.valueOf(sb));
        }


    }


    private static class ParseApiResponse {

        private String apiQuery = "";
        private String apiResponse = "";
        private int responseCode = 0;
        private List<String> unsyncedLyrics = null;
        private List<RichLyrics> syncedLyrics = null;

        public List<String> getUnSyncedLyrics() {
            return unsyncedLyrics;
        }

        public List<RichLyrics> getSyncedLyrics() {
            return syncedLyrics;
        }

        public void setUnSyncedLyrics(List<String> unsyncedLyric) {
            unsyncedLyrics = unsyncedLyric;
        }

        public void setSyncedLyrics(List<RichLyrics> syncedLyric) {
            syncedLyrics = syncedLyric;
        }

        final String userAgent =
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";


        public ParseApiResponse(String ApiQuery) throws IOException {
            this.apiQuery = ApiQuery;
            getResponse();
            getResponseCode();
            fetchUnsyncedLyrics();
            fetchSyncedLyrics();
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
            responseCode = Integer.parseInt(lyricsStatusCode);
        }

        protected void fetchUnsyncedLyrics() {
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
            setUnSyncedLyrics(lyrics);
        }

        protected void fetchSyncedLyrics() {
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
            setSyncedLyrics(lyrics);
        }

    }


}
