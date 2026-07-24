package com.kthowns.mobidic.storage.statistic.jparepository;

import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordStatisticJpaRepository extends JpaRepository<WordStatisticJpaEntity, UUID> {
    @Query("SELECT COALESCE((1.0 * SUM(" +
            "case when ws.isLearned = true then 1 else 0 end" +
            ")) / count(ws), 0.0)" +
            " from WordStatisticJpaEntity ws" +
            " join WordJpaEntity w on ws.wordId = w.id" +
            " where w.vocabulary.id = :vocabularyId and w.vocabulary.userId = :userId")
    double getVocabularyLearningRate(@Param("vocabularyId") UUID vocabularyId, @Param("userId") UUID userId);

    @Query("SELECT ws FROM WordStatisticJpaEntity ws" +
            " JOIN WordJpaEntity w on ws.wordId = w.id" +
            " WHERE w.vocabulary.userId = :userId")
    List<WordStatisticJpaEntity> findByUserId(UUID userId);

    @Query("SELECT ws FROM WordStatisticJpaEntity ws" +
            " JOIN WordJpaEntity w ON ws.wordId = w.id" +
            " WHERE w.vocabulary.userId = :userId" +
            " AND w.vocabulary.id = :vocabularyId")
    List<WordStatisticJpaEntity> findByVocabularyIdAndUserId(UUID vocabularyId, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ws FROM WordStatisticJpaEntity ws" +
            " JOIN WordJpaEntity w ON ws.wordId = w.id" +
            " WHERE w.id = :wordId" +
            " AND w.vocabulary.userId = :userId")
    Optional<WordStatisticJpaEntity> findForUpdate(
            @Param("wordId") UUID wordId,
            @Param("userId") UUID userId
    );

    @Query("SELECT ws FROM WordStatisticJpaEntity ws" +
            " JOIN WordJpaEntity w ON ws.wordId = w.id" +
            " WHERE w.id = :wordId" +
            " AND w.vocabulary.userId = :userId")
    Optional<WordStatisticJpaEntity> findByWordIdAndUserId(UUID wordId, UUID userId);
}
