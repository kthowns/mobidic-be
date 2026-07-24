package com.kthowns.mobidic.storage.statistic.jpaentity;

import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "word_statistics")
public class WordStatisticJpaEntity extends BaseAuditingEntity {
    @Id
    @Column(name = "word_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID wordId;

    @Column(name = "correct_count", nullable = false)
    private long correctCount;

    @Column(name = "incorrect_count", nullable = false)
    private long incorrectCount;

    @Column(name = "difficulty", nullable = false)
    private double difficulty;

    @Column(name = "accuracy", nullable = false)
    private double accuracy;

    @Column(name = "is_learned", nullable = false)
    private boolean isLearned;

    public static WordStatisticJpaEntity createFromModel(WordStatistic wordStatistic) {
        return new WordStatisticJpaEntity(
                wordStatistic.wordId(),
                0L,
                0L,
                0.0,
                0.0,
                false
        );
    }

    public void updateFromModel(WordStatistic wordStatistic) {
        this.correctCount = wordStatistic.correctCount();
        this.incorrectCount = wordStatistic.incorrectCount();
        this.difficulty = wordStatistic.difficulty();
        this.accuracy = wordStatistic.accuracy();
        this.isLearned = wordStatistic.isLearned();
    }

    public WordStatistic toModel() {
        return new WordStatistic(
                this.wordId,
                this.correctCount,
                this.incorrectCount,
                this.isLearned,
                this.difficulty,
                this.accuracy,
                AuditTime.of(getCreatedAt(), getUpdatedAt())
        );
    }

    @Builder(access = AccessLevel.PRIVATE)
    private WordStatisticJpaEntity(
            UUID wordId,
            long correctCount,
            long incorrectCount,
            double difficulty,
            double accuracy,
            boolean isLearned
    ) {
        this.wordId = wordId;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.difficulty = difficulty;
        this.accuracy = accuracy;
        this.isLearned = isLearned;
    }
}
