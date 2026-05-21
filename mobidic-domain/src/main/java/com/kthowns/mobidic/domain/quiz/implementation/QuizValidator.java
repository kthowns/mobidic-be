package com.kthowns.mobidic.domain.quiz.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizValidator {
    private final QuizRepository quizRepository;
    private static final String QUIZ_PREFIX = "quiz";

    public void validateQuizKey(String key) {
        if (key == null || !key.startsWith(QUIZ_PREFIX)) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST);
        }
        if (!quizRepository.exists(key)) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }
    }
}
