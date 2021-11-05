package com.absan.kopi.utils;

import com.absan.kopi.Spotify.Endpoints;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


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
    public static void skipAd() {
        try {
            SpotifyState.kill();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            SpotifyState.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSpotifyOpen() throws IOException {
        String line;
        boolean spotifyState = false;
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

        return spotifyState;

    }

    public static String getCurrentSongName() {
        String line;
        final List<String> songName = new ArrayList();
        try {
            Process proc = Runtime.getRuntime().exec("tasklist /fi \"imagename eq Spotify.exe\" /fo list /v");
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains("Window Title:") && !line.contains("N/A") && !line.contains("AngleHiddenWindow")) {
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

        if (!CurrentSong.currentSong.equals("Advertisement") && !CurrentSong.currentSong.equals("Spotify not opened") && !CurrentSong.currentSong.equals("Spotify Free")) {

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("q=");
            queryBuilder.append(URLEncoder.encode("track:", StandardCharsets.UTF_8));
            queryBuilder.append(URLEncoder.encode("\"" + CurrentSong.currentSong.split(" - ")[1] + "\"", StandardCharsets.UTF_8));
//            queryBuilder.append(URLEncoder.encode("+artist:", StandardCharsets.UTF_8));
//            queryBuilder.append(URLEncoder.encode("\"" + CurrentSong.currentSong.split(" - ")[0] + "\"", StandardCharsets.UTF_8));
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


//            TODO: Fix for songs with no image in items array

            allItems.forEach(item -> {
                if (String.valueOf(item).contains(CurrentSong.currentSong.split(" - ")[1]) &&
                        String.valueOf(item).contains(CurrentSong.currentSong.split(" - ")[0]) &&
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

            songId = songURIs.get(0);
            imageLink = imageLinks.get(0);

            System.out.println(songId);
            System.out.println(imageLink);
        }
    }

}


