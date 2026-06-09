package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.properties.QuizRedisKey;
import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QuizRemover {
    private final QuizAnswerRepository quizAnswerRepository;

    public void remove(String token) {
        String key = QuizRedisKey.QUIZ + ":" + token;
        quizAnswerRepository.remove(key);
    }
}
