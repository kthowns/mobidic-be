package com.kthowns.mobidic.api.quiz.util;

import com.kthowns.mobidic.api.quiz.type.QuizType;

public final class QuizGeneratorFactory {
    private static final QuizGenerator BLANK = new BlankQuizGenerator();
    private static final QuizGenerator OX = new OXQuizGenerator();

    public static QuizGenerator get(QuizType type) {
        return switch (type) {
            case QuizType.BLANK -> BLANK;
            case QuizType.OX -> OX;
        };
    }
}

