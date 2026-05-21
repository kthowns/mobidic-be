package com.kthowns.mobidic.domain.quiz.implementation;

import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizRemover {
    private final QuizRepository quizRepository;

    public void removeAnswer(String key) {
        quizRepository.deleteAnswer(key);
    }
}
