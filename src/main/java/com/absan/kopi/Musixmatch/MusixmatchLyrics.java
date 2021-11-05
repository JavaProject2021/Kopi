package com.absan.kopi.Musixmatch;

import com.absan.kopi.utils.CurrentSong;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.layout.Pane;
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
    String artistName;
    String songName;
    public static List<String> musixmatchUnsyncedLyrics = new ArrayList<String>();
    public static List<RichLyrics> musixmatchSyncedLyrics = new ArrayList<RichLyrics>();
    public static String mfinalLyrics = "";

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
        artistName = CurrentSong.currentSong.split(" - ")[0];
        songName = CurrentSong.currentSong.split(" - ")[1];
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

        ParseApiResponse variable = new ParseApiResponse(String.valueOf(musixmatchQuery));
        musixmatchUnsyncedLyrics = variable.unsyncedLyrics;
        musixmatchSyncedLyrics = variable.syncedLyrics;

        StringBuilder sb = new StringBuilder();
        musixmatchUnsyncedLyrics.forEach(line -> {
            sb.append(line).append("\n");
        });

        mfinalLyrics = String.valueOf(sb);

    }


}
