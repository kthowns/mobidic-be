package com.kthowns.mobidic.api.preset.service;

import com.kthowns.mobidic.api.dictionary.entity.Definition;
import com.kthowns.mobidic.api.dictionary.entity.Vocabulary;
import com.kthowns.mobidic.api.dictionary.entity.Word;
import com.kthowns.mobidic.api.dictionary.repository.DefinitionRepository;
import com.kthowns.mobidic.api.dictionary.repository.VocabularyRepository;
import com.kthowns.mobidic.api.dictionary.repository.WordRepository;
import com.kthowns.mobidic.api.preset.entity.PresetDefinition;
import com.kthowns.mobidic.api.preset.entity.PresetVocabulary;
import com.kthowns.mobidic.api.preset.entity.PresetWord;
import com.kthowns.mobidic.api.preset.repository.PresetVocabularyRepository;
import com.kthowns.mobidic.api.statistic.entity.WordStatistic;
import com.kthowns.mobidic.api.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.api.user.entity.User;
import com.kthowns.mobidic.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresetVocabularyService {
    private final PresetVocabularyRepository presetVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final WordRepository wordRepository;
    private final DefinitionRepository definitionRepository;
    private final UserRepository userRepository;
    private final WordStatisticRepository wordStatisticRepository;

    // Total 쿼리 약 70개 (단어장 3개, 단어장 당 단어 10개, 단어 당 뜻 1개)
    @Transactional
    public void copyAllPresetToUser(User user) {
        User managedUser = userRepository.getReferenceById(user.getId());
        if (vocabularyRepository.existsByUser(managedUser)) return;

        // 프리셋 전체 로드 (Batch Size 덕분에 N+1 방어됨)
        List<PresetVocabulary> presetVocabs = presetVocabularyRepository.findAll();
        // Vocabulary 먼저 생성 및 저장 (ID 확보를 위해 Flush 필수)
        List<Vocabulary> vocabularies = presetVocabs.stream()
                .map(pv -> Vocabulary.builder()
                        .user(managedUser)
                        .title(pv.getTitle())
                        .description(pv.getDescription())
                        .wordCount((long) pv.getWords().size())
                        .build())
                .toList();
        vocabularyRepository.saveAllAndFlush(vocabularies);

        // 루프 하나에서 Word와 Definition을 동시에 수집
        List<Word> allWords = new ArrayList<>();
        List<Definition> allDefinitions = new ArrayList<>();
        List<WordStatistic> allWordStatistics = new ArrayList<>();

        for (int i = 0; i < presetVocabs.size(); i++) {
            PresetVocabulary pv = presetVocabs.get(i);
            Vocabulary v = vocabularies.get(i); // 인덱스로 매칭 (1:1이니까 안전)

            for (PresetWord pw : pv.getWords()) {
                // Word 생성
                Word w = Word.builder()
                        .vocabulary(v)
                        .expression(pw.getExpression())
                        .build();
                allWords.add(w);

                WordStatistic ws = WordStatistic.builder()
                        .word(w)
                        .build();
                allWordStatistics.add(ws);

                // Definition 생성 (wordPointer 따위 필요 없음)
                for (PresetDefinition pd : pw.getDefinitions()) {
                    allDefinitions.add(Definition.builder()
                            .word(w) // 방금 만든 Word 객체를 그대로 사용
                            .meaning(pd.getMeaning())
                            .part(pd.getPart())
                            .build());
                }
            }
        }

        wordRepository.saveAllAndFlush(allWords);
        wordStatisticRepository.saveAllAndFlush(allWordStatistics);
        definitionRepository.saveAll(allDefinitions);
    }
}
