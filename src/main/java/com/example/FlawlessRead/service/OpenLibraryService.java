package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.Book;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenLibraryService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Book> searchBooks(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://openlibrary.org/search.json?q=" + encodedQuery;

        OpenLibraryResponse response = restTemplate.getForObject(url, OpenLibraryResponse.class);
        List<Book> books = new ArrayList<>();

        if (response != null && response.docs != null) {
            for (Doc doc : response.docs) {
                Book book = new Book();
                book.setTitulo(doc.title);
                book.setAutor(doc.authorName != null && !doc.authorName.isEmpty() ? doc.authorName.get(0) : "Autor desconhecido");
                book.setIsbn(doc.isbn != null && !doc.isbn.isEmpty() ? doc.isbn.get(0) : null);
                book.setCapaUrl(doc.coverI != null ? "https://covers.openlibrary.org/b/id/" + doc.coverI + "-L.jpg" : null);
                book.setPublishDate(doc.firstPublishYear != null ? LocalDate.of(doc.firstPublishYear, 1, 1) : null);
                book.setGenero(doc.subject != null && !doc.subject.isEmpty() ? doc.subject.get(0) : null);
                book.setKey(doc.key != null && !doc.key.isEmpty() ? doc.key : null);


                books.add(book);
            }
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
