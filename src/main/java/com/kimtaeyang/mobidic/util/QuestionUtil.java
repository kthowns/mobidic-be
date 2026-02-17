package com.kimtaeyang.mobidic.util;

import com.kimtaeyang.mobidic.dto.QuestionRateDto;
import com.kimtaeyang.mobidic.entity.Question;
import com.kimtaeyang.mobidic.model.WordWithDefs;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
public class QuestionUtil {
    public static List<Question> generateQuiz(UUID memberId, QuestionStrategy strategy, List<WordWithDefs> wordsWithDefs) {
        return strategy.generateQuestions(memberId, wordsWithDefs);
    }
}
