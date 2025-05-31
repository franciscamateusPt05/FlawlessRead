package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.AudioFeatures;
import com.example.FlawlessRead.model.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private static final String SPOTIFY_API_BASE = "https://api.spotify.com/v1";
    private static final String OPEN_LIBRARY_API_BASE = "https://openlibrary.org";

    private final WebClient webClient;
    private final SpotifyAuthService spotifyAuthService;
    private final ObjectMapper objectMapper;

    public SearchService(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
        this.webClient = WebClient.create();
        this.objectMapper = new ObjectMapper();
    }

    private Mono<JsonNode> fetchFromSpotify(String endpoint) {
        return spotifyAuthService.getValidAccessToken()
                .flatMap(token ->
                        webClient.get()
                                .uri(SPOTIFY_API_BASE + endpoint)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(JsonNode.class)
                );
    }

    public Mono<AudioFeatures> getAudioFeatures(String trackId) {
        return fetchFromSpotify("/audio-features/" + trackId)
                .map(node -> new AudioFeatures(
                        node.get("danceability").asDouble(),
                        node.get("energy").asDouble(),
                        node.get("valence").asDouble(),
                        node.get("acousticness").asDouble(),
                        node.get("instrumentalness").asDouble()
                ));
    }

    public Mono<List<String>> getArtistGenres(String artistId) {
        return fetchFromSpotify("/artists/" + artistId)
                .map(node -> {
                    List<String> genres = new ArrayList<>();
                    node.get("genres").forEach(g -> genres.add(g.asText()));
                    return genres;
                });
    }

    public Mono<List<String>> identifySubjects(AudioFeatures features, List<String> artistGenres) {
        return Mono.fromCallable(() -> {
            InputStream is = getClass().getResourceAsStream("/static/mapping.json");
            if (is == null) {
                throw new RuntimeException("Could not load mapping.json");
            }

            JsonNode mapping = objectMapper.readTree(is);
            List<String> matchedSubjects = new ArrayList<>();

            mapping.fields().forEachRemaining(entry -> {
                String subject = entry.getKey();
                JsonNode subjectNode = entry.getValue();
                List<String> styles = objectMapper.convertValue(
                        subjectNode.get("music_styles"),
                        new TypeReference<List<String>>() {}
                );
                JsonNode af = subjectNode.get("audio_features");

                int score = 0;
                if (!Collections.disjoint(styles, artistGenres)) score++;
                if (matchesFeatures(features, af)) score++;

                if (score >= 1) matchedSubjects.add(subject);
            });

            return matchedSubjects;
        });
    }

    private boolean matchesFeatures(AudioFeatures features, JsonNode featureRanges) {
        return featureInRange(features.getDanceability(), featureRanges.get("danceability")) &&
                featureInRange(features.getEnergy(), featureRanges.get("energy")) &&
                featureInRange(features.getValence(), featureRanges.get("valence")) &&
                featureInRange(features.getAcousticness(), featureRanges.get("acousticness")) &&
                featureInRange(features.getInstrumentalness(), featureRanges.get("instrumentalness"));
    }

    private boolean featureInRange(double value, JsonNode range) {
        return value >= range.get(0).asDouble() && value <= range.get(1).asDouble();
    }

    public Mono<List<Book>> fetchBooksFromOpenLibrary(List<String> subjects) {
        return Flux.fromIterable(subjects)
                .flatMap(subject -> {
                    String url = OPEN_LIBRARY_API_BASE + "/subjects/" + subject.toLowerCase().replace(" ", "_") + ".json?limit=5";
                    return webClient.get()
                            .uri(url)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .onErrorResume(e -> Mono.empty()) // ignora erros individuais
                            .flatMapMany(response -> {
                                if (response == null || !response.has("works")) {
                                    return Flux.empty();
                                }

                                return Flux.fromIterable(response.get("works"))
                                        .map(work -> {
                                            String title = work.path("title").asText("Unknown Title");
                                            String author = work.path("authors").path(0).path("name").asText("Unknown Author");
                                            String isbn = work.path("isbn").path(0).asText(null);
                                            String coverId = work.path("cover_id").asText(null);
                                            String key = work.path("key").asText(null);
                                            int publishYear = work.path("first_publish_year").asInt(0);

                                            return new Book(
                                                    UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                                                    title,
                                                    author,
                                                    isbn,
                                                    subject,
                                                    coverId != null ? "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg" : null,
                                                    publishYear > 0 ? LocalDate.of(publishYear, 1, 1) : null,
                                                    key
                                            );
                                        });
                            });
                })
                .collectList();
    }

    public Mono<List<Book>> processMusicToBooks(String trackId, String artistId) {
        return Mono.zip(getAudioFeatures(trackId), getArtistGenres(artistId))
                .flatMap(tuple -> {
                    AudioFeatures features = tuple.getT1();
                    List<String> genres = tuple.getT2();
                    return identifySubjects(features, genres);
                })
                .flatMap(this::fetchBooksFromOpenLibrary);
    }

    public Mono<String[]> searchTrackAndArtistId(String trackName, String artistName) {
        String query = "track:" + trackName + " artist:" + artistName;
        String encodedQuery = UriUtils.encode(query, StandardCharsets.UTF_8);
        return fetchFromSpotify("/search?q=" + encodedQuery + "&type=track&limit=1")
                .map(response -> {
                    JsonNode firstTrack = response.path("tracks").path("items").path(0);
                    if (firstTrack.isMissingNode()) {
                        throw new RuntimeException("No tracks found for: " + trackName + " by " + artistName);
                    }
                    String trackId = firstTrack.path("id").asText();
                    String artistId = firstTrack.path("artists").path(0).path("id").asText();
                    return new String[]{trackId, artistId};
                });
    }
}
