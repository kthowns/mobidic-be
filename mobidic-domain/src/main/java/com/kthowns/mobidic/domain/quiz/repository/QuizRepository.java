package com.kthowns.mobidic.domain.quiz.repository;

public interface QuizRepository {
    void appendAnswer(String key, String answer, long expMillis);

    String getAnswer(String key);

    void deleteAnswer(String key);

    boolean exists(String key);
}
