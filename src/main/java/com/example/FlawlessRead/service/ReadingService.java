package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.UserBook;
import com.example.FlawlessRead.repository.UserBookRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReadingService {

    private final UserBookRepository userBookRepository;

    public ReadingService(UserBookRepository userBookRepository) {
        this.userBookRepository = userBookRepository;
    }

    public List<UserBook> getBooksByUserId(Long userId) {
        return userBookRepository.findByUserId(userId);
    }

    // Exemplo: contar livros lidos
    public int countBooksReadByUser(Long userId) {
        List<UserBook> books = getBooksByUserId(userId);
        return books.size();
    }

    // Exemplo: ler histórico leitura por mês (ano-mês)
    public Map<String, Integer> getReadingHistory(Long userId) {
        List<UserBook> books = getBooksByUserId(userId);

        Map<String, Integer> history = new TreeMap<>(); // ordenar por data (String yyyy-MM)

        for (UserBook ub : books) {
            String month = ub.getDataAdicionado().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            history.put(month, history.getOrDefault(month, 0) + 1);
        }

        // Acumular os valores para uma série temporal acumulada
        int acumulado = 0;
        Map<String, Integer> acumuladoHistory = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : history.entrySet()) {
            acumulado += entry.getValue();
            acumuladoHistory.put(entry.getKey(), acumulado);
        }

        return acumuladoHistory;
    }

    public Map<String, Integer> getBooksByGenreByUser(Long userId) {
        List<UserBook> books = getBooksByUserId(userId);

        Map<String, Integer> booksByGenre = new HashMap<>();

        for (UserBook ub : books) {
            String genre = ub.getBook().getGenero();
            booksByGenre.put(genre, booksByGenre.getOrDefault(genre, 0) + 1);
        }

        return booksByGenre;
    }

}

