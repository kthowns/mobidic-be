package com.kimtaeyang.mobidic.quiz.util;

import com.kimtaeyang.mobidic.quiz.type.QuizType;

public final class QuizGeneratorFactory {
    private static final QuizGenerator BLANK = new BlankQuizGenerator();
    private static final QuizGenerator OX = new OXQuizGenerator();

    private QuizGeneratorFactory() {}

    public static QuizGenerator get(QuizType type) {
        return switch (type) {
            case BLANK -> BLANK;
            case OX -> OX;
        };
    }
}

