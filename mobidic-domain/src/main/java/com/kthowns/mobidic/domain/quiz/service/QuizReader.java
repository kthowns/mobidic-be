package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QuizReader {
    private final QuizRepository quizRepository;

    public String readAnswer(String key) {
        String answer = quizRepository.getAnswer(key);
        if (answer == null) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }
        return answer;
    }
}
