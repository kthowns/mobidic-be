package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.QuizType;

final class QuizGeneratorFactory {
    private static final QuizGenerator BLANK = new BlankQuizGenerator();
    private static final QuizGenerator OX = new OXQuizGenerator();

    public static QuizGenerator get(QuizType type) {
        return switch (type) {
            case QuizType.BLANK -> BLANK;
            case QuizType.OX -> OX;
        };
    }
}

