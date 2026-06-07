package com.kthowns.mobidic.domain.quiz.properties;

import org.springframework.stereotype.Component;

@Component
public class QuizProperties {
    //@Value("${quiz.exp-per-quiz:15000}")
    private final Long expPerQuiz = 15000L;

    public long getExpPerQuiz() {
        return expPerQuiz;
    }
}
