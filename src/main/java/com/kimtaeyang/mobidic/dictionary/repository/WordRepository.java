package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WordRepository extends JpaRepository<Word, UUID> {
    List<Word> findByVocabulary(Vocabulary vocabulary);

    int countByExpressionAndVocabularyAndIdNot(String expression, Vocabulary vocabulary, UUID id);

    int countByExpressionAndVocabulary(String expression, Vocabulary vocabulary);

    long countByVocabulary(Vocabulary vocabulary);
}
