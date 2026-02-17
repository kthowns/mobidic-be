package com.kimtaeyang.mobidic.util;

import com.kimtaeyang.mobidic.dto.DefDto;
import com.kimtaeyang.mobidic.entity.Question;
import com.kimtaeyang.mobidic.model.WordWithDefs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OxQuestionStrategy extends QuestionStrategy {
    @Override
    public List<Question> generateQuestions(UUID memberId, List<WordWithDefs> orgWordsWithDefs) {
        List<WordWithDefs> wordsWithDefs = new ArrayList<>(orgWordsWithDefs);
        derange(wordsWithDefs);

        ArrayList<String> options = new ArrayList<>();
        ArrayList<Question> questions = new ArrayList<>();

        for (WordWithDefs wordWithDefs : wordsWithDefs) {
            String option = "";

            if (wordWithDefs.getDefDtos() != null && !wordWithDefs.getDefDtos().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordWithDefs.getDefDtos().size());
                option = wordWithDefs.getDefDtos().get(randIdx).getDefinition();
            }

            options.add(option); //단어당 랜덤한 하나의 뜻 추출하여 options에 저장
        }
        partialShuffle((options.size() / 2) + 1, options);

        for (int i = 0; i < wordsWithDefs.size(); i++) {
            String answer = "0";

            List<String> defs = wordsWithDefs.get(i).getDefDtos().stream()
                    .map(DefDto::getDefinition).toList();

            if (defs.contains(options.get(i))) {
                answer = "1";
            }

            questions.add(
                    Question.builder()
                            .id(UUID.randomUUID())
                            .wordId(wordsWithDefs.get(i).getWordDto().getId())
                            .memberId(memberId)
                            .stem(wordsWithDefs.get(i).getWordDto().getExpression())
                            .answer(answer)
                            .options(List.of(options.get(i)))
                            .build()
            );
        }

        return questions;
    }
}
