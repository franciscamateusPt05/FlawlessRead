package com.example.FlawlessRead.service;
import com.example.FlawlessRead.model.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RecommendationService {

    // Podes mover isso para config ou um ficheiro JSON
    private static final Map<String, Map<String, GenreInfo>> genres = new HashMap<>();

    static {
        Map<String, GenreInfo> fiction = new HashMap<>();
        fiction.put("Fantasy", new GenreInfo(
                Arrays.asList("ambient", "classical", "soundtracks", "folk", "electronic"),
                new double[][]{{0.2, 0.5},{0.3, 0.6},{0.4, 0.7},{0.3, 0.7},{0.4, 0.8}}
        ));
        fiction.put("Romance", new GenreInfo(
                Arrays.asList("pop", "r-n-b", "soul", "acoustic", "indie-pop"),
                new double[][]{{0.4, 0.7},{0.3, 0.6},{0.6, 0.9},{0.3, 0.6},{0.0, 0.3}}
        ));
        genres.put("Fiction", fiction);
    }

    public RecommendationResponse getRecommendations(String songName, String artistName) throws Exception {
        // Aqui usas o token real (depois implementa OAuth)
        String token = getSpotifyAccessToken();

        // Buscar audio features e estilo do artista (simulado)
        AudioFeatures features = getAudioFeatures(songName, artistName, token);
        String artistStyle = getArtistStyle(artistName, token);

        List<String> matchedGenres = mapMusicToGenres(artistStyle, features);

        ArrayList<Book> booksByGenre = new ArrayList<>() ;

        for (String genre : matchedGenres) {
            Book book =new Book();
//            book.setTitulo(titulo);
//            book.setAutor(autor);
//            book.setIsbn(isbn);
//            book.setGenero(genero);
//            book.setCapaUrl(capa);
            booksByGenre.add(book);
        }

        RecommendationResponse response = new RecommendationResponse();
        response.setGenres(matchedGenres);
        response.setBooksByGenre(booksByGenre);

        return response;
    }

    private String getSpotifyAccessToken() {
        // TODO: implementar autenticação real
        return "Bearer SEU_TOKEN_DE_ACESSO";
    }

    private AudioFeatures getAudioFeatures(String song, String artist, String token) {
        // Simulado
        return new AudioFeatures(0.5, 0.5, 0.6, 0.5, 0.4);
    }

    private String getArtistStyle(String artist, String token) {
        // Simulado
        return "pop";
    }

    private List<String> mapMusicToGenres(String musicStyle, AudioFeatures features) {
        List<String> matchedGenres = new ArrayList<>();

        for (Map.Entry<String, Map<String, GenreInfo>> mainCat : genres.entrySet()) {
            for (Map.Entry<String, GenreInfo> subGenre : mainCat.getValue().entrySet()) {
                GenreInfo info = subGenre.getValue();
                if (info.musicStyles.contains(musicStyle.toLowerCase())
                        && inRange(features, info.audioFeatures)) {
                    matchedGenres.add(subGenre.getKey());
                }
            }
        }
        return matchedGenres;
    }

    private boolean inRange(AudioFeatures f, double[][] ranges) {
        return inRangeCheck(f.danceability, ranges[0]) &&
                inRangeCheck(f.energy, ranges[1]) &&
                inRangeCheck(f.valence, ranges[2]) &&
                inRangeCheck(f.acousticness, ranges[3]) &&
                inRangeCheck(f.instrumentalness, ranges[4]);
    }

    private boolean inRangeCheck(double value, double[] range) {
        return value >= range[0] && value <= range[1];
    }

    private List<String> searchBooksByGenre(String genre) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://openlibrary.org/subjects/" + genre + ".json?limit=5";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        List<String> titles = new ArrayList<>();

        if (root.has("works")) {
            for (JsonNode work : root.get("works")) {
                titles.add(work.get("title").asText());
            }
        }
        return titles;
    }

    // Classes auxiliares
    private static class GenreInfo {
        List<String> musicStyles;
        double[][] audioFeatures;

        public GenreInfo(List<String> musicStyles, double[][] audioFeatures) {
            this.musicStyles = musicStyles;
            this.audioFeatures = audioFeatures;
        }
    }

    private static class AudioFeatures {
        double danceability;
        double energy;
        double valence;
        double acousticness;
        double instrumentalness;

        public AudioFeatures(double danceability, double energy, double valence, double acousticness, double instrumentalness) {
            this.danceability = danceability;
            this.energy = energy;
            this.valence = valence;
            this.acousticness = acousticness;
            this.instrumentalness = instrumentalness;
        }
    }
}

