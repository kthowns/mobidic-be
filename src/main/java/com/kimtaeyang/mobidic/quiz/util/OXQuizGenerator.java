package com.kimtaeyang.mobidic.quiz.util;

import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.quiz.model.Quiz;
import com.kimtaeyang.mobidic.dictionary.model.WordWithDefs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OXQuizGenerator extends QuizGenerator {
    @Override
    public List<Quiz> generate(UUID memberId, List<WordWithDefs> orgWordsWithDefs) {
        List<WordWithDefs> wordsWithDefs = new ArrayList<>(orgWordsWithDefs);
        derange(wordsWithDefs);

        ArrayList<String> options = new ArrayList<>();
        ArrayList<Quiz> quizzes = new ArrayList<>();

        for (WordWithDefs wordWithDefs : wordsWithDefs) {
            String option = "";

            if (wordWithDefs.getDefinitionDtos() != null && !wordWithDefs.getDefinitionDtos().isEmpty()) {
                int randIdx = ThreadLocalRandom.current().nextInt(wordWithDefs.getDefinitionDtos().size());
                option = wordWithDefs.getDefinitionDtos().get(randIdx).getDefinition();
            }

            options.add(option); //단어당 랜덤한 하나의 뜻 추출하여 options에 저장
        }
        partialShuffle((options.size() / 2) + 1, options);

        for (int i = 0; i < wordsWithDefs.size(); i++) {
            String answer = "0";

            List<String> defs = wordsWithDefs.get(i).getDefinitionDtos().stream()
                    .map(DefinitionDto::getDefinition).toList();

            if (defs.contains(options.get(i))) {
                answer = "1";
            }

            quizzes.add(
                    Quiz.builder()
                            .id(UUID.randomUUID())
                            .wordId(wordsWithDefs.get(i).getWordDto().getId())
                            .memberId(memberId)
                            .stem(wordsWithDefs.get(i).getWordDto().getExpression())
                            .answer(answer)
                            .options(List.of(options.get(i)))
                            .build()
            );
        }

        return quizzes;
    }
}
