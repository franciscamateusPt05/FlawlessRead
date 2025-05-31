package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.AudioFeatures;
import com.example.FlawlessRead.model.SpotifySearchResponse;
import com.example.FlawlessRead.model.Suggestion;
import com.example.FlawlessRead.service.SpotifyAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spotify")
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;
    private final WebClient webClient;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService, WebClient.Builder webClientBuilder) {
        this.spotifyAuthService = spotifyAuthService;
        this.webClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String url = spotifyAuthService.getAuthorizationUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    @GetMapping("/callback")
    public Mono<ResponseEntity<Map<String, Object>>> callback(@RequestParam String code, @RequestParam String state) {
        return spotifyAuthService.exchangeCodeForTokens(code)
                .map(tokens -> ResponseEntity.ok().body(tokens));
    }

    @GetMapping("/userinfo")
    public Mono<String> getUserInfo(@RequestHeader("Authorization") String bearerToken) {
        return spotifyAuthService.webClient.get()
                .uri("https://api.spotify.com/v1/me")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/suggestions")
    public Mono<List<Suggestion>> getSuggestions(@RequestParam("q") String query) {
        return spotifyAuthService.getValidAccessToken() // método que retorna Mono<String> do token
                .flatMap(bearerToken ->
                        webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/search")
                                        .queryParam("q", query)
                                        .queryParam("type", "track")
                                        .queryParam("limit", 5)
                                        .build())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                                .retrieve()
                                .bodyToMono(SpotifySearchResponse.class)
                                .map(response -> response.getTracks().getItems().stream()
                                        .map(track -> new Suggestion(track.getName(), track.getArtists().get(0).getName()))
                                        .collect(Collectors.toList()))
                );
    }


    @GetMapping("/audio-features/{id}")
    public Mono<AudioFeatures> getAudioFeatures(@PathVariable String id,
                                                @RequestHeader("Authorization") String bearerToken) {
        return webClient.get()
                .uri("/audio-features/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(AudioFeatures.class);
    }

    @PostMapping("/refresh")
    public Mono<Map<String, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isBlank()) {
            return Mono.error(new IllegalArgumentException("Refresh token é obrigatório."));
        }
        return spotifyAuthService.refreshAccessToken(refreshToken);
    }
}
