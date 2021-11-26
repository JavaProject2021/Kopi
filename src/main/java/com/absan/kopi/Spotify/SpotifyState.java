package com.absan.kopi.Spotify;

import com.absan.kopi.utils.CurrentSong;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import commands.MediaKeys;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;


import static com.absan.kopi.Main.tokenGetter;

public class SpotifyState {
    public static String imageLink;
    public static String songId;

    public static void kill() throws IOException {
        Runtime.getRuntime().exec("taskkill /F /IM Spotify.exe");
    }

    public static void start() throws IOException {
        Runtime.getRuntime().exec(System.getenv("APPDATA") + "\\Spotify\\Spotify.exe");
    }

    //    TODO: Make a method to close and open spotify and resume playing song with windows sound server
    public static void skipAd() throws AWTException {
        try {
            SpotifyState.kill();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            SpotifyState.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MediaKeys.songPlayPause();
        MediaKeys.songNext();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
    }

    public static boolean isSpotifyOpen() {
        String line;
        boolean spotifyState = false;
        try {

            Process proc = Runtime.getRuntime().exec("tasklist /fi \"imagename eq Spotify.exe\" /fo list /v");
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains("Window Title:") && !line.contains("N/A") && !line.contains("AngleHiddenWindow")) {
                    spotifyState = true;
                    break;
                }
            }
            input.close();
            proc.destroy();
        } catch (Exception ignored) {

        }

        return spotifyState;

    }

    public static String getCurrentSongName() {
        String line;
        final List<String> songName = new ArrayList();
        try {
            Process proc = Runtime.getRuntime().exec("tasklist /fi \"imagename eq Spotify.exe\" /fo list /v");
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains("Window Title:") && !line.contains("N/A") && !line.contains("AngleHiddenWindow") && !line.contains("GDI+ Window (Spotify.exe)") && !line.contains("OleMainThreadWndName")) {
                    assert false;
                    songName.add(line.replace("Window Title:", "").stripLeading().stripTrailing());
                }
            }
            input.close();
            proc.destroy();
            if (songName.size() != 0) {
                return songName.get(0);
            } else {
                songName.add("Spotify not opened");
                return songName.get(0);
            }
        } catch (IndexOutOfBoundsException | IOException ioe) {
            songName.add("Error");
        } finally {
            songName.add("Not playing");
        }
        return songName.get(0);
    }

    public static void getSongId() throws IOException {

        if (CurrentSong.getCurrentSong().split(" - ").length > 1 && !CurrentSong.getCurrentSong().equals("Advertisement") && !CurrentSong.getCurrentSong().equals("Spotify not opened") && !CurrentSong.getCurrentSong().equals("Spotify Free")) {

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("q=");
            queryBuilder.append(URLEncoder.encode("track:", StandardCharsets.UTF_8));
            queryBuilder.append(URLEncoder.encode("\"" + CurrentSong.getCurrentSong().split(" - ")[1] + "\"", StandardCharsets.UTF_8));
            queryBuilder.append("&type=track&offset=0&limit=10");

            URL url = new URL(Endpoints.SEARCH + queryBuilder);

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + tokenGetter.getToken().replaceAll("\"", ""));

            BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String currentLine = Lines.readLine();
            StringBuilder response = new StringBuilder();
            while (currentLine != null) {
                response.append(currentLine).append("\n");
                currentLine = Lines.readLine();
            }

            http.disconnect();

            String expectedValue = "";
            String json = String.valueOf(response);
            expectedValue = String.valueOf(JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("tracks"));
            JsonArray allItems = JsonParser.parseString(expectedValue).getAsJsonObject().getAsJsonArray("items");

            ArrayList<String> imageLinks = new ArrayList<>();
            ArrayList<String> songURIs = new ArrayList<>();


            allItems.forEach(item -> {
                if (String.valueOf(item).contains(CurrentSong.getCurrentSong().split(" - ")[1]) &&
                        String.valueOf(item).contains(CurrentSong.getCurrentSong().split(" - ")[0]) &&
                        allItems.size() != 1) {

                    String link = String.valueOf(JsonParser.parseString(String.valueOf(item)).getAsJsonObject().getAsJsonObject("album"));
                    songURIs.add(String.valueOf(JsonParser.parseString(String.valueOf(item)).getAsJsonObject().get("uri")));
                    link = String.valueOf(JsonParser.parseString(link).getAsJsonObject().getAsJsonArray("images").get(0));
                    link = String.valueOf(JsonParser.parseString(link).getAsJsonObject().get("url")).replaceAll("\"", "");
                    imageLinks.add(link);
                } else {
                    String link = String.valueOf(JsonParser.parseString(String.valueOf(item)).getAsJsonObject().getAsJsonObject("album"));
                    songURIs.add(String.valueOf(JsonParser.parseString(String.valueOf(item)).getAsJsonObject().get("uri")));
                    link = String.valueOf(JsonParser.parseString(link).getAsJsonObject().getAsJsonArray("images").get(0));
                    link = String.valueOf(JsonParser.parseString(link).getAsJsonObject().get("url")).replaceAll("\"", "");
                    imageLinks.add(link);
                }
            });

            if (songURIs.size() != 0) {
                songId = songURIs.get(0);
            } else {
                songId = "";
            }
            if (imageLinks.size() != 0) {
                imageLink = imageLinks.get(0);
            } else {
                imageLink = "https://picsum.photos/536/354";
            }

        }
    }

}


