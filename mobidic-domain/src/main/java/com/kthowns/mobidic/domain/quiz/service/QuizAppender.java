package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QuizAppender {
    private final QuizRepository quizRepository;

    public void saveAnswer(String key, String answer, long expMillis) {
        quizRepository.appendAnswer(key, answer, expMillis);
    }
}
