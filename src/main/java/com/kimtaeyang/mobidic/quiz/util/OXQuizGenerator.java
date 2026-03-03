package com.kimtaeyang.mobidic.quiz.util;

import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDetail;
import com.kimtaeyang.mobidic.quiz.model.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OXQuizGenerator extends QuizGenerator {
    @Override
    public List<Quiz> generate(UUID memberId, List<WordDetail> orgWordDetails) {
        List<WordDetail> wordDetails = new ArrayList<>(orgWordDetails);
        derange(wordDetails);

        ArrayList<String> options = new ArrayList<>();
        ArrayList<Quiz> quizzes = new ArrayList<>();

        for (WordDetail wordDetail : wordDetails) {
            String option = "";

            if (wordDetail.definitions() != null && !wordDetail.definitions().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordDetail.definitions().size());
                option = wordDetail.definitions().get(randIdx).getMeaning();
            }

            options.add(option); //단어당 랜덤한 하나의 뜻 추출하여 options에 저장
        }
        partialShuffle((options.size() / 2) + 1, options);

        for (int i = 0; i < wordDetails.size(); i++) {
            String answer = "0";

            List<String> defs = wordDetails.get(i).definitions().stream()
                    .map(DefinitionDto::getMeaning).toList();

            if (defs.contains(options.get(i))) {
                answer = "1";
            }

            quizzes.add(
                    Quiz.builder()
                            .id(UUID.randomUUID())
                            .wordId(wordDetails.get(i).id())
                            .userId(memberId)
                            .stem(wordDetails.get(i).expression())
                            .answer(answer)
                            .options(List.of(options.get(i)))
                            .build()
            );
        }

        return quizzes;
    }
}
