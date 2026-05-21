package com.kthowns.mobidic.storage.statistic.jparepository;

import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordStatisticJpaRepository extends JpaRepository<WordStatisticJpaEntity, UUID> {
    @Query("select (1.0 * sum(" +
            "case when ws.isLearned = true then 1 else 0 end" +
            ")) / count(ws)" +
            " from WordStatisticJpaEntity ws" +
            " where ws.word.vocabulary = :vocabulary")
    Optional<Double> getVocabularyLearningRate(@Param("vocabulary") VocabularyJpaEntity vocabulary);

    List<WordStatisticJpaEntity> findByWord_Vocabulary_User_Id(UUID userId);

    List<WordStatisticJpaEntity> findByWord_Vocabulary_Id(UUID vocabularyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ws from WordStatisticJpaEntity ws where ws.word.id = :wordId and ws.word.vocabulary.user.id = :userId")
    Optional<WordStatisticJpaEntity> findForUpdate(@Param("wordId") UUID wordId, @Param("userId") UUID userId);

    Optional<WordStatisticJpaEntity> findByWordIdAndWord_Vocabulary_User_Id(UUID wordId, UUID wordVocabularyUserId);
}
