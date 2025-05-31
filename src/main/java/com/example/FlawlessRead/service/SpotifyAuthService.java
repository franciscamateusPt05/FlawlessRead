package com.example.FlawlessRead.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
@Service
public class SpotifyAuthService {

    private final String clientId = "a6b6d1adc15c4fffa5adfa578ed5991c";
    private final String clientSecret = "0834b13878134957a4d4b84e8d2be652";
    private final String redirectUri = "http://localhost:8080/spotify/callback";

    public final WebClient webClient = WebClient.builder().build();

    public String getAuthorizationUrl() {
        String scopes = "user-read-private user-read-email user-read-playback-state user-read-recently-played";

        return "https://accounts.spotify.com/authorize?" +
                "client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scopes.replace(" ", "%20") +
                "&state=xyz"; // ideal validar state
    }

    public Mono<Map<String, Object>> exchangeCodeForTokens(String code) {
        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        String body = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + redirectUri;

        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + base64Auth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});  // <== Aqui
    }

    public Mono<Map<String, Object>> refreshAccessToken(String refreshToken) {
        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        String body = "grant_type=refresh_token" +
                "&refresh_token=" + refreshToken;

        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + base64Auth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public Mono<String> getValidAccessToken() {
        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
        String body = "grant_type=client_credentials";

        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + base64Auth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    if (response == null || !response.containsKey("access_token")) {
                        throw new RuntimeException("Failed to obtain access token from Spotify.");
                    }
                    return response.get("access_token").toString();
                });
    }



}
