package com.kthowns.mobidic.domain.quiz.repository;

import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;

import java.util.Optional;

public interface QuizAnswerRepository {
    void append(String key, QuizAnswer quizAnswer, long expMillis);

    Optional<QuizAnswer> read(String key);

    void remove(String key);
}
