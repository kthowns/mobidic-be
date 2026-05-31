package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QuizValidator {
    private final QuizRepository quizRepository;

    public void validateQuizKey(String key) {
        if (key == null || !key.startsWith(QuizRedisKey.QUIZ.getPrefix())) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST);
        }
        if (!quizRepository.exists(key)) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }
    }
}
