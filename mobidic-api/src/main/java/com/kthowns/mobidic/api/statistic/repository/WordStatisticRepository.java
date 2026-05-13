package com.kthowns.mobidic.api.statistic.repository;

import com.kthowns.mobidic.api.dictionary.entity.Vocabulary;
import com.kthowns.mobidic.api.statistic.entity.WordStatistic;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    List<WordStatistic> findByWord_Vocabulary_User_Id(UUID userId);

    List<WordStatistic> findByWord_Vocabulary_Id(UUID vocabularyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ws from WordStatistic ws where ws.word.id = :wordId and ws.word.vocabulary.user.id = :userId")
    Optional<WordStatistic> findForUpdate(@Param("wordId") UUID wordId, @Param("userId") UUID userId);

    Optional<WordStatistic> findByWordIdAndWord_Vocabulary_User_Id(UUID wordId, UUID wordVocabularyUserId);
}
