package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WordRepository extends JpaRepository<Word, UUID>, WordRepositoryCustom {
    boolean existsByExpressionAndVocabularyAndIdNot(String expression, Vocabulary vocabulary, UUID id);

    long countByVocabulary(Vocabulary vocabulary);

    Optional<Word> findByIdAndVocabulary_User_Id(UUID id, UUID userId);

    boolean existsByExpressionAndVocabulary(String expression, Vocabulary vocabulary);
}
