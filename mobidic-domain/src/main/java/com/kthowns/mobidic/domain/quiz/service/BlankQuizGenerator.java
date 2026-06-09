package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.word.model.WordDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

class BlankQuizGenerator extends QuizGenerator {
    @Override
    public List<Quiz> generate(UUID memberId, List<WordDetail> orgWordDetails) {
        List<WordDetail> wordDetails = new ArrayList<>(orgWordDetails);
        derange(wordDetails);

        //option은 뜻
        ArrayList<Quiz> quizzes = new ArrayList<>(wordDetails.size());

        for (WordDetail wordDetail : wordDetails) {
            String option = "";

            if (wordDetail.definitions() != null && !wordDetail.definitions().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordDetail.definitions().size());
                option = wordDetail.definitions().get(randIdx).meaning();
            }

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
                            .options(List.of(option))
                            .build()
            );
        }

        return quizzes;
    }
}
