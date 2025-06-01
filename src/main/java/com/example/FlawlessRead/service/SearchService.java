package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public Mono<Set<String>> getGenresFromArtistAndTrack(String trackId, String artistId) {
        Mono<Set<String>> artistGenresMono = getArtistGenres(artistId)
                .doOnNext(genres -> System.out.println("Gêneros do artista principal (" + artistId + "): " + genres));

        Mono<Set<String>> additionalArtistsGenresMono = fetchFromSpotify("/tracks/" + trackId)
                .flatMap(trackNode -> {
                    List<String> additionalArtistIds = new ArrayList<>();
                    trackNode.path("artists").forEach(artist -> {
                        String id = artist.path("id").asText();
                        if (!id.equals(artistId)) {
                            additionalArtistIds.add(id);
                        }
                    });

                    return Flux.fromIterable(additionalArtistIds)
                            .flatMap(this::getArtistGenres)
                            .flatMapIterable(genres -> genres)
                            .collect(Collectors.toSet())
                            .doOnNext(genres -> System.out.println("Gêneros dos artistas adicionais: " + genres));
                });

        return Mono.zip(artistGenresMono, additionalArtistsGenresMono)
                .map(tuple -> {
                    Set<String> combined = new HashSet<>(tuple.getT1());
                    combined.addAll(tuple.getT2());
                    System.out.println("Gêneros combinados do artista e track: " + combined);
                    return combined;
                });
    }

    public Mono<List<String>> identifySubjects(Set<String> combinedGenres) {
        return Mono.fromCallable(() -> {
            System.out.println("Identificando subjects a partir dos gêneros: " + combinedGenres);
            InputStream is = getClass().getResourceAsStream("/static/mapping.json");
            if (is == null) {
                throw new RuntimeException("Could not load mapping.json");
            }

            JsonNode mapping = objectMapper.readTree(is);

            // Map<subject, score>
            Map<String, Integer> subjectScores = new HashMap<>();

            mapping.fields().forEachRemaining(entry -> {
                String subject = entry.getKey();
                JsonNode subjectNode = entry.getValue();
                List<String> styles = objectMapper.convertValue(
                        subjectNode.get("music_styles"),
                        new TypeReference<List<String>>() {}
                );

                int score = 0;
                for (String style : styles) {
                    if (combinedGenres.contains(style.toLowerCase())) {
                        score++;
                        System.out.println("Match parcial para subject " + subject + " pelo estilo: " + style);
                    }
                }

                if (score > 0) {
                    subjectScores.put(subject, score);
                    System.out.println("Subject " + subject + " com score: " + score);
                }
            });

            // Ordenar pelo score descrescente
            List<String> matchedSubjects = subjectScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // Se não encontrou nenhum com score > 0, pode retornar todos ou algum padrão
            if (matchedSubjects.isEmpty()) {
                System.out.println("Nenhum subject encontrado com interseção, retornando todos os subjects.");
                matchedSubjects = new ArrayList<>();
                mapping.fieldNames().forEachRemaining(matchedSubjects::add);
            }

            System.out.println("Subjects identificados finais ordenados por prioridade: " + matchedSubjects);
            return matchedSubjects;
        });
    }



    public Mono<Set<String>> getArtistGenres(String artistId) {
        return fetchFromSpotify("/artists/" + artistId)
                .map(node -> {
                    Set<String> genres = new HashSet<>();
                    node.get("genres").forEach(g -> genres.add(g.asText()));
                    return genres;
                });
    }

    public Mono<List<Book>> fetchBooksFromOpenLibrary(List<String> subjects) {
        return Flux.fromIterable(subjects)
                .flatMap(subject -> {
                    String formattedSubject = subject.toLowerCase().replace(" ", "_");
                    String url = String.format("%s/subjects/%s.json?limit=5", OPEN_LIBRARY_API_BASE, formattedSubject);

                    return webClient.get()
                            .uri(url)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .onErrorResume(e -> Mono.empty())
                            .flatMapMany(json -> {
                                if (json == null || !json.has("works")) return Flux.empty();

                                return Flux.fromIterable(json.get("works"))
                                        .map(work -> {
                                            String title = work.path("title").asText("Sem título");

                                            String author = "Desconhecido";
                                            if (work.has("authors") && work.get("authors").isArray() && work.get("authors").size() > 0) {
                                                author = work.get("authors").get(0).path("name").asText("Desconhecido");
                                            }

                                            String isbn = "";
                                            if (work.has("isbn") && work.get("isbn").isArray() && work.get("isbn").size() > 0) {
                                                isbn = work.get("isbn").get(0).asText();
                                            }

                                            if (isbn.isEmpty()) {
                                                isbn = "unknown_" + UUID.randomUUID();
                                            }

                                            String key = work.path("key").asText(null);
                                            int publishYear = work.path("first_publish_year").asInt(0);
                                            LocalDate dataPublicacao = null;
                                            if (publishYear >= 1000 && publishYear <= LocalDate.now().getYear()) {
                                                dataPublicacao = LocalDate.of(publishYear, 1, 1);
                                            }

                                            String coverId = work.has("cover_id") ? work.get("cover_id").asText(null) : null;
                                            String capaUrl = coverId != null ? "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg" : null;

                                            return new Book(
                                                    UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                                                    title,
                                                    author,
                                                    isbn,
                                                    subject,
                                                    capaUrl,
                                                    dataPublicacao,
                                                    key
                                            );
                                        });
                            });
                })
                .distinct(Book::getKey)
                .collectList()
                .map(bookList -> {
                    bookList.sort(Comparator.comparing(
                            Book::getPublishDate,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ));
                    return bookList;
                });
    }

    public Mono<List<Book>> processMusicToBooks(String trackId, String artistId) {
        return getGenresFromArtistAndTrack(trackId, artistId)
                .flatMap(this::identifySubjects)
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
