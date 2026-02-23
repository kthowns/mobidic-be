package com.kimtaeyang.mobidic.statistic.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordStatisticRepository extends JpaRepository<WordStatistic, UUID> {
    @Query("select (1.0 * sum(" +
            "case when ws.isLearned = true then 1 else 0 end" +
            ")) / count(ws)" +
            " from WordStatistic ws" +
            " where ws.word.vocabulary = :vocabulary")
    Optional<Double> getVocabularyLearningRate(@Param("vocabulary") Vocabulary vocabulary);

    @Modifying
    @Query("update WordStatistic ws " +
            " set ws.correctCount = ws.correctCount + 1" +
            " where ws.word = :word")
    void increaseCorrectCount(@Param("word") Word word);

    @Modifying
    @Query("update WordStatistic ws " +
            " set ws.incorrectCount = ws.incorrectCount + 1" +
            " where ws.word = :word")
    void increaseIncorrectCount(@Param("word") Word word);

    List<WordStatistic> findByWord_Vocabulary_User_Id(UUID userId);

    List<WordStatistic> findByWord_Vocabulary_Id(UUID vocabularyId);

    Optional<WordStatistic> findByWordIdAndWord_Vocabulary_User_Id(UUID id, UUID userId);
}
