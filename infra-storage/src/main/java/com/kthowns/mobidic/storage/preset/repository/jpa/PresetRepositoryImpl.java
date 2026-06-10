package com.kthowns.mobidic.storage.preset.repository.jpa;

import com.kthowns.mobidic.domain.preset.repository.PresetRepository;
import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.definition.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.preset.jpaentity.PresetVocabularyJpaEntity;
import com.kthowns.mobidic.storage.preset.jparepository.PresetVocabularyJpaRepository;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import com.kthowns.mobidic.storage.word.jparepository.WordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PresetRepositoryImpl implements PresetRepository {
    private final PresetVocabularyJpaRepository presetVocabularyJpaRepository;
    private final VocabularyJpaRepository vocabularyJpaRepository;
    private final WordJpaRepository wordJpaRepository;
    private final DefinitionJpaRepository definitionJpaRepository;
    private final WordStatisticJpaRepository wordStatisticJpaRepository;

    @Override
    public void copyAllPresetsToUser(UUID userId) {
        UserJpaEntity user = UserJpaEntity.builder().id(userId).build();
        List<PresetVocabularyJpaEntity> presetVocabs = presetVocabularyJpaRepository.findAll();

        List<VocabularyJpaEntity> vocabularies = presetVocabs.stream()
                .map(pv -> VocabularyJpaEntity.builder()
                        .user(user)
                        .title(pv.getTitle())
                        .description(pv.getDescription())
                        .wordCount((long) pv.getWords().size())
                        .build())
                .toList();
        vocabularyJpaRepository.saveAllAndFlush(vocabularies);

        List<WordJpaEntity> allWords = new ArrayList<>();
        List<DefinitionJpaEntity> allDefinitions = new ArrayList<>();
        List<WordStatisticJpaEntity> allWordStatistics = new ArrayList<>();

        for (int i = 0; i < presetVocabs.size(); i++) {
            PresetVocabularyJpaEntity pv = presetVocabs.get(i);
            VocabularyJpaEntity v = vocabularies.get(i);

            for (var pw : pv.getWords()) {
                WordJpaEntity w = WordJpaEntity.builder()
                        .vocabulary(v)
                        .expression(pw.getExpression())
                        .build();
                allWords.add(w);

                allWordStatistics.add(WordStatisticJpaEntity.builder()
                        .word(w)
                        .build());

                for (var pd : pw.getDefinitions()) {
                    allDefinitions.add(DefinitionJpaEntity.builder()
                            .word(w)
                            .meaning(pd.getMeaning())
                            .part(pd.getPart())
                            .build());
                }
            }
        }

        wordJpaRepository.saveAllAndFlush(allWords);
        wordStatisticJpaRepository.saveAllAndFlush(allWordStatistics);
        definitionJpaRepository.saveAll(allDefinitions);
    }
}
