package com.kimtaeyang.mobidic.statistic.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.statistic.entity.Statistic;
import com.kimtaeyang.mobidic.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {
    @Query("select (1.0*sum(r.isLearned)) / count(w)" +
            " from Word w join Statistic r" +
            " on w = r.word" +
            " where w.vocabulary = :vocabulary")
    Optional<Double> getVocabLearningRate(@Param("vocab") Vocabulary vocabulary);

    Optional<Statistic> findRateByWord(Word word);

    @Modifying
    @Query("update Statistic r " +
            " set r.correctCount = r.correctCount + 1" +
            " where r.word = :word")
    void increaseCorrectCount(@Param("word") Word word);

    @Modifying
    @Query("update Statistic r " +
            " set r.incorrectCount = r.incorrectCount + 1" +
            " where r.word = :word")
    void increaseIncorrectCount(@Param("word") Word word);

    @Query("select r from Statistic r" +
            " join Word w on w.id = r.wordId" +
            " where w.vocabulary = :vocabulary")
    List<Statistic> findByVocab(Vocabulary vocabulary);

    @Query("select r from Statistic r" +
            " join Word w on w.id = r.wordId" +
            " join Vocabulary v on v.id = w.vocabulary.id" +
            " where v.user = :user")
    List<Statistic> findByMember(User user);
}
