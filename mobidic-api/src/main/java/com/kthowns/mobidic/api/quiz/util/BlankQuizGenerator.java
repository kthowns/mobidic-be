package com.kthowns.mobidic.api.quiz.util;

import com.kthowns.mobidic.api.dictionary.dto.WordDetail;
import com.kthowns.mobidic.api.quiz.model.Quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BlankQuizGenerator extends QuizGenerator {
    @Override
    public List<Quiz> generate(UUID memberId, List<WordDetail> orgWordDetails) {
        List<WordDetail> wordDetails = new ArrayList<>(orgWordDetails);
        derange(wordDetails);

        //option은 뜻
        ArrayList<String> options = new ArrayList<>();
        ArrayList<Quiz> quizzes = new ArrayList<>(wordDetails.size());

        for (WordDetail wordDetail : wordDetails) {
            String option = "";

            if (wordDetail.definitions() != null && !wordDetail.definitions().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordDetail.definitions().size());
                option = wordDetail.definitions().get(randIdx).getMeaning();
            }

            options.add(option);

            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < wordDetail.expression().length(); i++) {
                nums.add(i);
            }
            int blankCount = nums.size() / 2 + 1;
            derange(nums);

            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < blankCount; i++) {
                indices.add(nums.get(i));
            }
            Collections.sort(indices);

            char[] stem = wordDetail.expression().toCharArray();

            for (int idx : indices) {
                stem[idx] = '_';
            }

            quizzes.add(
                    Quiz.builder()
                            .id(UUID.randomUUID())
                            .wordId(wordDetail.id())
                            .userId(memberId)
                            .stem(new String(stem))
                            .answer(wordDetail.expression())
                            .build()
            );
        }

        for (int i = 0; i < wordDetails.size(); i++) {
            quizzes.get(i).setOptions(List.of(options.get(i)));
        }

        return quizzes;
    }
}
