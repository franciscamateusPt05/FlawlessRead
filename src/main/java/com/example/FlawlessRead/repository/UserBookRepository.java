package com.example.FlawlessRead.repository;

import com.example.FlawlessRead.model.UserBook;
import com.example.FlawlessRead.model.UserBookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, UserBookId> {
    // Buscar todos os UserBook pelo userId (na chave composta)
    @Query("SELECT ub FROM UserBook ub WHERE ub.id.userId = :userId")
    List<UserBook> findByUserId(@Param("userId") Long userId);
}

