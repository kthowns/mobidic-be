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
            " where ws.word.vocabulary.id = :vocabularyId and ws.word.vocabulary.user.id = :userId")
    double getVocabularyLearningRate(@Param("vocabularyId") UUID vocabularyId, @Param("userId") UUID userId);

    List<WordStatisticJpaEntity> findByWord_Vocabulary_User_Id(UUID userId);

    List<WordStatisticJpaEntity> findByWord_Vocabulary_IdAndWord_Vocabulary_User_Id(UUID vocabularyId, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ws from WordStatisticJpaEntity ws where ws.word.id = :wordId and ws.word.vocabulary.user.id = :userId")
    Optional<WordStatisticJpaEntity> findForUpdate(@Param("wordId") UUID wordId, @Param("userId") UUID userId);

    Optional<WordStatisticJpaEntity> findByWordIdAndWord_Vocabulary_User_Id(UUID wordId, UUID wordVocabularyUserId);
}
