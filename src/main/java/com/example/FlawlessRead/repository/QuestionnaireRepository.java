package com.example.FlawlessRead.repository;

import com.example.FlawlessRead.model.Questionnaire;
import com.example.FlawlessRead.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    Optional<Questionnaire> findByUserId(Long userId);

    Questionnaire findByUser(Optional<User> user);
}

