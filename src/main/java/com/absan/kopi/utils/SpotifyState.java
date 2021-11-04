package com.absan.kopi.utils;

import com.absan.kopi.Spotify.Endpoints;
import com.absan.kopi.Spotify.SpotifyToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.absan.kopi.Main.tokenGetter;

public class SpotifyState {

    public static void kill() throws IOException {
        Runtime.getRuntime().exec("taskkill /F /IM Spotify.exe");
    }

    public static void start() throws IOException {
        Runtime.getRuntime().exec(System.getenv("APPDATA") + "\\Spotify\\Spotify.exe");
    }

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
            String query = "q=" + URLEncoder.encode("track:\"" + CurrentSong.currentSong.split(" - ")[1] + "\"", StandardCharsets.UTF_8).replace("+", "%20") +
                    URLEncoder.encode("+artist:\"" + CurrentSong.currentSong.split(" - ")[0] + "\"", StandardCharsets.UTF_8).replace("+", "%20") +
                    "&type=track&offset=0&limit=20";

            URL url = new URL(Endpoints.SEARCH + query);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + tokenGetter.accessToken);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage() + " " + Endpoints.SEARCH + query);
            http.disconnect();
        }

    }

}
