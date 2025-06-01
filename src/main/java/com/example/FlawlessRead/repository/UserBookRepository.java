package com.example.FlawlessRead.repository;

import com.example.FlawlessRead.model.UserBook;
import com.example.FlawlessRead.model.UserBookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, UserBookId> {
    // Aqui você já herda o método save(UserBook entity)
}
