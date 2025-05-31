package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.*;
import com.example.FlawlessRead.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class QuestionnaireController {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository; // supomos que tens uma entidade Questionnaire
    private Model model;

    public QuestionnaireController(UserRepository userRepository, QuestionnaireRepository questionnaireRepository) {
        this.userRepository = userRepository;
        this.questionnaireRepository = questionnaireRepository;
    }

    @GetMapping("/questionnaire")
    public String showQuestionnaire(@RequestParam Long user, Model model) {
        User currentUser = userRepository.findById(user).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", currentUser);
        return "questionnaire";  // template HTML para o questionário
    }

    @PostMapping("/questionnaire")
    public String processQuestionnaire(
            @RequestParam Long userId,
            @RequestParam(name = "generosPreferidos", required = false) String[] generosPreferidos,
            Model model) {

        User currentUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setUser(currentUser);

        if (generosPreferidos != null) {
            String joinedGenres = String.join(",", generosPreferidos);
            questionnaire.setGenerosPreferidos(joinedGenres);
        } else {
            questionnaire.setGenerosPreferidos("");
        }

        questionnaireRepository.save(questionnaire);

        return "redirect:/login";
    }


}

