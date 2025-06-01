package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.Book;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OpenLibraryService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String fields = URLEncoder.encode("title,author_name,isbn,key,first_publish_year", StandardCharsets.UTF_8);
            String urlString = String.format(
                    "https://openlibrary.org/search.json?q=%s&fields=%s&limit=12&lang=en",
                    encodedQuery, fields
            );

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                JsonNode root = objectMapper.readTree(responseBuilder.toString());

                if (root.has("docs")) {
                    for (JsonNode bookNode : root.get("docs")) {
                        String titulo = bookNode.path("title").asText("Sem título");

                        String autor = "Desconhecido";
                        if (bookNode.has("author_name") && bookNode.get("author_name").isArray()) {
                            autor = bookNode.get("author_name").get(0).asText("Desconhecido");
                        }

                        String isbn = "";
                        if (bookNode.has("isbn") && bookNode.get("isbn").isArray()) {
                            isbn = bookNode.get("isbn").get(0).asText();
                        }

                        if (isbn.isEmpty()) {
                            isbn = "unknown_" + UUID.randomUUID();
                        }

                        String key = bookNode.has("key") ? bookNode.get("key").asText() : null;

                        int publishYear = bookNode.path("first_publish_year").asInt(0);
                        LocalDate dataPublicacao = null;
                        if (publishYear >= 1000 && publishYear <= LocalDate.now().getYear()) {
                            dataPublicacao = LocalDate.of(publishYear, 1, 1);
                        }

                        // Gênero: não vem no search direto, então assumimos "Desconhecido"
                        String genero = "Desconhecido";

                        String capa = "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";

                        Book book = new Book(
                                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                                titulo,
                                autor,
                                isbn,
                                genero,
                                capa,
                                dataPublicacao,
                                key
                        );

                        books.add(book);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar livros com query: " + query + " - " + e.getMessage());
        }
        return books;
    }



    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenLibraryResponse {
        public List<Doc> docs;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Doc {
        public String title;

        @JsonProperty("author_name")
        public List<String> authorName;

        @JsonProperty("first_publish_year")
        public Integer firstPublishYear;

        public List<String> isbn;

        @JsonProperty("cover_i")
        public Integer coverI;

        public List<String> subject;

        public String key; // ex: "/works/OL12345W"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WorkDetail {
        public Object description; // pode ser String ou objeto com campo "value"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DescriptionObject {
        public String value;
    }

    public String getBookDescription(String key) {
        try {
            String url = "https://openlibrary.org" + key + ".json";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.get("description") != null) {
                Object desc = response.get("description");
                if (desc instanceof String) return (String) desc;
                if (desc instanceof Map) return (String) ((Map<?, ?>) desc).get("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Descrição não disponível.";
    }

}
