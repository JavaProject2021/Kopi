package com.absan.kopi.Musixmatch;

public class RichLyrics {
    String Lyrics;
    Double Total;
    Double Minutes;
    Double Seconds;
    Double Hundreths;

    RichLyrics(String Lyrics,
               Double Total,
               Double Minutes,
               Double Seconds,
               Double Hundredths) {

        this.Hundreths = Hundredths;
        this.Lyrics = Lyrics;
        this.Minutes = Minutes;
        this.Seconds = Seconds;
        this.Total = Total;
    }

    public void printLyrics() {
        System.out.println(this.Minutes + ":" + this.Seconds + ":" + this.Hundreths + "(" + this.Total + ")  :" + Lyrics);
    }
}
