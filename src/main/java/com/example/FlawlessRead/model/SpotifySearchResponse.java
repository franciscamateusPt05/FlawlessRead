package com.example.FlawlessRead.model;

import java.util.List;

// DTO classes para deserializar resposta da API Spotify
public class SpotifySearchResponse {
    private Tracks tracks;

    public SpotifySearchResponse() {} // construtor padrão

    public Tracks getTracks() { return tracks; }
    public void setTracks(Tracks tracks) { this.tracks = tracks; }

    public static class Tracks {
        private List<Track> items;

        public Tracks() {}

        public List<Track> getItems() { return items; }
        public void setItems(List<Track> items) { this.items = items; }
    }

    public static class Track {
        private String name;
        private List<Artist> artists;

        public Track() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<Artist> getArtists() { return artists; }
        public void setArtists(List<Artist> artists) { this.artists = artists; }
    }

    public static class Artist {
        private String name;

        public Artist() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
