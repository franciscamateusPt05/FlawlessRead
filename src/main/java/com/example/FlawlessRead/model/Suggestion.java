package com.example.FlawlessRead.model;

public class Suggestion {
    private String name;
    private String artist;

    public Suggestion() {}  // construtor padrão

    public Suggestion(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
}
