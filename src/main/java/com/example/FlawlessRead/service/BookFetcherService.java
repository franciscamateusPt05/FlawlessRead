package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.BookNewest;
import com.example.FlawlessRead.model.BookTrending;
import com.example.FlawlessRead.repository.BookNewestRepository;
import com.example.FlawlessRead.repository.BookTrendingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class BookFetcherService {

    private final BookNewestRepository newestRepo;
    private final BookTrendingRepository trendingRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String[] generos = {"fantasy", "romance", "fiction", "mystery"};

    public BookFetcherService(BookNewestRepository newestRepo, BookTrendingRepository trendingRepo) {
        this.newestRepo = newestRepo;
        this.trendingRepo = trendingRepo;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void fetchBooksDaily() {
        fetchTrendingBooks();
        fetchNewestBooks();
    }

    private void fetchTrendingBooks() {
        trendingRepo.deleteAll();
        for (String genero : generos) {
            fetchBooksTrend(genero);
        }
    }

    private void fetchNewestBooks() {
        newestRepo.deleteAll();
        for (String genero : generos) {
            fetchBooksByGenreOpenLibrary(genero, "new");
        }
    }

    private void fetchBooksByGenreOpenLibrary(String genero, String sortParam) {
        try {
            String generoEncoded = URLEncoder.encode("subject:" + genero, StandardCharsets.UTF_8);
            String fields = URLEncoder.encode("title,author_name,isbn,availability", StandardCharsets.UTF_8);

            String urlString = String.format(
                    "https://openlibrary.org/search.json?q=%s&fields=%s&sort=%s&limit=12&lang=en",
                    generoEncoded, fields, URLEncoder.encode(sortParam, StandardCharsets.UTF_8)
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

                        String capa = getOpenLibraryCover(isbn);

                            if (!newestRepo.existsByIsbn(isbn)) {
                                BookNewest book = new BookNewest();
                                book.setTitulo(titulo);
                                book.setAutor(autor);
                                book.setIsbn(isbn);
                                book.setGenero(genero);
                                book.setCapaUrl(capa);
                                newestRepo.save(book);
                                System.out.println("Livro novo guardado: " + titulo);
                            }
                        }
                    }

            } else {
                System.err.println("Erro HTTP " + responseCode + " ao buscar livros de " + genero + " com sort=" + sortParam);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar livros de " + genero  + e.getMessage());
        }
    }

    private void fetchBooksTrend(String genero) {
        try {
            String generoEncoded = URLEncoder.encode("subject:" + genero, StandardCharsets.UTF_8);
            String fields = URLEncoder.encode("title,author_name,isbn,availability", StandardCharsets.UTF_8);

            String urlString = String.format(
                    "https://openlibrary.org/search.json?q=%s&fields=%s&&limit=12&lang=en",
                    generoEncoded, fields
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

                        String capa = getOpenLibraryCover(isbn);

                        if (!trendingRepo.existsByIsbn(isbn)) {
                                BookTrending book = new BookTrending();
                                book.setTitulo(titulo);
                                book.setAutor(autor);
                                book.setIsbn(isbn);
                                book.setGenero(genero);
                                book.setCapaUrl(capa);
                                trendingRepo.save(book);
                                System.out.println("Livro trending guardado: " + titulo);
                            }

                        }
                    }

            } else {
                System.err.println("Erro HTTP " + responseCode + " ao buscar livros de " + genero );
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar livros de " + genero  + e.getMessage());
        }
    }

    private String getOpenLibraryCover(String isbn) {
        if (isbn.startsWith("unknown_")) {
            return "";
        }
        return "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
    }
}
