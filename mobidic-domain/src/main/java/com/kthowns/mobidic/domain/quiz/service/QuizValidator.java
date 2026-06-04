package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class QuizValidator {

    public void validateOwnership(QuizAnswer quizAnswer, UUID userId) {
        if (!quizAnswer.userId().equals(userId)) {
            throw new ApiException(GeneralResponseCode.NO_QUIZ);
        }
    }
}
