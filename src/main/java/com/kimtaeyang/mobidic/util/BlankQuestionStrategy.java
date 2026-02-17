package com.kimtaeyang.mobidic.util;

import com.kimtaeyang.mobidic.entity.Question;
import com.kimtaeyang.mobidic.model.WordWithDefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BlankQuestionStrategy extends QuestionStrategy {
    @Override
    public List<Question> generateQuestions(UUID memberId, List<WordWithDefs> orgWordsWithDefs) {
        List<WordWithDefs> wordsWithDefs = new ArrayList<>(orgWordsWithDefs);
        derange(wordsWithDefs);

        //option은 뜻
        ArrayList<String> options = new ArrayList<>();
        ArrayList<Question> questions = new ArrayList<>(wordsWithDefs.size());

        for (WordWithDefs wordWithDefs : wordsWithDefs) {
            String option = "";

            if (wordWithDefs.getDefDtos() != null && !wordWithDefs.getDefDtos().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordWithDefs.getDefDtos().size());
                option = wordWithDefs.getDefDtos().get(randIdx).getDefinition();
            }

            options.add(option);

            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < wordWithDefs.getWordDto().getExpression().length(); i++) {
                nums.add(i);
            }
            int blankCount = nums.size() / 2 + 1;
            derange(nums);

            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < blankCount; i++) {
                indices.add(nums.get(i));
            }
            Collections.sort(indices);

            char[] stem = wordWithDefs.getWordDto().getExpression().toCharArray();
            for (int idx : indices) {
                stem[idx] = '_';
            }

            questions.add(
                    Question.builder()
                            .id(UUID.randomUUID())
                            .wordId(wordWithDefs.getWordDto().getId())
                            .memberId(memberId)
                            .stem(new String(stem))
                            .answer(wordWithDefs.getWordDto().getExpression())
                            .build()
            );
        }

        for (int i = 0; i < wordsWithDefs.size(); i++) {
            questions.get(i).setOptions(List.of(options.get(i)));
        }

        return questions;
    }
}
