package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import com.kthowns.mobidic.domain.quiz.properties.QuizRedisKey;
import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class QuizAppender {
    private final QuizAnswerRepository quizAnswerRepository;

    public String append(UUID userId, Quiz quiz, long expMillis) {
        String token = UUID.randomUUID().toString();
        String key = QuizRedisKey.QUIZ + ":" + token;

        QuizAnswer quizAnswer = QuizAnswer
                .of(userId, quiz.wordId(), quiz.id(), quiz.answer());

        quizAnswerRepository.append(key, quizAnswer, expMillis);
        return token;
    }
}
