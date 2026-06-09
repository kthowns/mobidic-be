package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import com.kthowns.mobidic.domain.quiz.properties.QuizRedisKey;
import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QuizReader {
    private final QuizAnswerRepository quizAnswerRepository;

    public QuizAnswer read(String token) {
        String key = QuizRedisKey.QUIZ + ":" + token;
        return quizAnswerRepository.read(key)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.REQUEST_TIMEOUT));
    }
}
