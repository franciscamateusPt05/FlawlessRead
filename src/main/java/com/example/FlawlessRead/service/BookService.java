package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.Book;
import com.example.FlawlessRead.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> fetchBooksByGenres(List<String> generos, String sortParam) {
        List<Book> books = new ArrayList<>();
        try {
            // Monta query com todos os generos unidos por OR
            StringBuilder queryBuilder = new StringBuilder();
            for (int i = 0; i < generos.size(); i++) {
                if (i > 0) {
                    queryBuilder.append(" OR ");
                }
                queryBuilder.append("subject:").append(generos.get(i));
            }

            String query = URLEncoder.encode(queryBuilder.toString(), StandardCharsets.UTF_8);

            // Campos extras: title, author_name, isbn, key, first_publish_year
            String fields = URLEncoder.encode("title,author_name,isbn,key,first_publish_year", StandardCharsets.UTF_8);

            String urlString = String.format(
                    "https://openlibrary.org/search.json?q=%s&fields=%s&sort=%s&limit=12&lang=en",
                    query, fields, URLEncoder.encode(sortParam, StandardCharsets.UTF_8)
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

                        // Key do livro
                        String key = "";
                        if (bookNode.has("key")) {
                            key = bookNode.get("key").asText();
                        }

                        // Ano de publicação
                        int publishYear = bookNode.path("first_publish_year").asInt(0);
                        LocalDate dataPublicacao = null;
                        if (publishYear >= 1000 && publishYear <= LocalDate.now().getYear()) {
                            dataPublicacao = LocalDate.of(publishYear, 1, 1);
                        }

                        // Descobrir o gênero do livro — o API não retorna diretamente, então
                        // aqui você pode definir como "Múltiplos" ou vazio
                        String genero = "Múltiplos";

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
            System.err.println("Erro ao buscar livros para os gêneros: " + generos + " - " + e.getMessage());
        }
        return books;
    }



    public List<Book> fetchBooksByGenres(List<String> generos) {
        List<Book> books = new ArrayList<>();

        for (String genero : generos) {
            try {
                String generoEncoded = URLEncoder.encode("subject:" + genero, StandardCharsets.UTF_8);
                // Adiciona campos extras: first_publish_year e key
                String fields = URLEncoder.encode("title,author_name,isbn,key,first_publish_year", StandardCharsets.UTF_8);

                String urlString = String.format(
                        "https://openlibrary.org/search.json?q=%s&fields=%s&limit=12&lang=en",
                        generoEncoded, fields);

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

                            // Pega a key (ex: "/works/OL12345W")
                            String key = "";
                            if (bookNode.has("key")) {
                                key = bookNode.get("key").asText();
                            }

                            // Ano de publicação
                            int publishYear = bookNode.path("first_publish_year").asInt(0);
                            LocalDate dataPublicacao = null;
                            if (publishYear >= 1000 && publishYear <= LocalDate.now().getYear()) {
                                dataPublicacao = LocalDate.of(publishYear, 1, 1);
                            }

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
                System.err.println("Erro ao buscar livros de " + genero + ": " + e.getMessage());
            }
        }
        return books;
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book findByIsbn(String isbn) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        return bookOpt.orElse(null);
    }



}
