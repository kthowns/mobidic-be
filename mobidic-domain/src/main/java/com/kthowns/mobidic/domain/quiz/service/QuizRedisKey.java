package com.kthowns.mobidic.domain.quiz.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizRedisKey {
    QUIZ("quiz");

    private final String prefix;

    @Override
    public String toString() {
        return prefix;
    }
}
