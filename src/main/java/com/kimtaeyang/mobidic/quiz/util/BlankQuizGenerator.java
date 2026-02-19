package com.kimtaeyang.mobidic.quiz.util;

import com.kimtaeyang.mobidic.quiz.model.Quiz;
import com.kimtaeyang.mobidic.dictionary.model.WordWithDefinitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BlankQuizGenerator extends QuizGenerator {
    @Override
    public List<Quiz> generate(UUID memberId, List<WordWithDefinitions> orgWordsWithDefs) {
        List<WordWithDefinitions> wordsWithDefs = new ArrayList<>(orgWordsWithDefs);
        derange(wordsWithDefs);

        //option은 뜻
        ArrayList<String> options = new ArrayList<>();
        ArrayList<Quiz> quizzes = new ArrayList<>(wordsWithDefs.size());

        for (WordWithDefinitions wordWithDefinitions : wordsWithDefs) {
            String option = "";

            if (wordWithDefinitions.getDefinitionDtos() != null && !wordWithDefinitions.getDefinitionDtos().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordWithDefinitions.getDefinitionDtos().size());
                option = wordWithDefinitions.getDefinitionDtos().get(randIdx).getDefinition();
            }

            options.add(option);

            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < wordWithDefinitions.getWordDto().getExpression().length(); i++) {
                nums.add(i);
            }
            int blankCount = nums.size() / 2 + 1;
            derange(nums);

            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < blankCount; i++) {
                indices.add(nums.get(i));
            }
            Collections.sort(indices);

            char[] stem = wordWithDefinitions.getWordDto().getExpression().toCharArray();
            for (int idx : indices) {
                stem[idx] = '_';
            }

            quizzes.add(
                    Quiz.builder()
                            .id(UUID.randomUUID())
                            .wordId(wordWithDefinitions.getWordDto().getId())
                            .userId(memberId)
                            .stem(new String(stem))
                            .answer(wordWithDefinitions.getWordDto().getExpression())
                            .build()
            );
        }

        for (int i = 0; i < wordsWithDefs.size(); i++) {
            quizzes.get(i).setOptions(List.of(options.get(i)));
        }

        return quizzes;
    }
}
