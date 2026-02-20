package com.kimtaeyang.mobidic.statistic.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import com.kimtaeyang.mobidic.user.entity.User;
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

    @Query("select ws from WordStatistic ws" +
            " join Word w on w.id = ws.wordId" +
            " where w.vocabulary = :vocabulary")
    List<WordStatistic> findByVocab(Vocabulary vocabulary);

    @Query("select ws from WordStatistic ws" +
            " join Word w on w.id = ws.wordId" +
            " join Vocabulary v on v.id = w.vocabulary.id" +
            " where v.user = :user")
    List<WordStatistic> findByMember(User user);
}
