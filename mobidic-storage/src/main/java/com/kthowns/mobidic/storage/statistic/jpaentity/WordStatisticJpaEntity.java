package com.kthowns.mobidic.storage.statistic.jpaentity;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "word_statistics")
public class WordStatisticJpaEntity {
    @Id
    private UUID wordId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WordJpaEntity word;

    @Column(name = "correct_count", nullable = false)
    @Builder.Default
    private long correctCount = 0L;

    @Column(name = "incorrect_count", nullable = false)
    @Builder.Default
    private long incorrectCount = 0L;

    @Column(name = "difficulty", nullable = false)
    @Builder.Default
    private double difficulty = 0.5;

    @Column(name = "accuracy", nullable = false)
    @Builder.Default
    private double accuracy = 0.0;

    @Column(name = "is_learned", nullable = false)
    @Builder.Default
    private boolean isLearned = false;

    public void update(Long correctCount, Long incorrectCount, boolean isLearned, double difficulty, double accuracy) {
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.isLearned = isLearned;
        this.difficulty = difficulty;
        this.accuracy = accuracy;
    }

    public WordStatistic toModel() {
        return WordStatistic.builder()
                .wordId(this.word.getId())
                .correctCount(this.correctCount)
                .incorrectCount(this.incorrectCount)
                .isLearned(this.isLearned)
                .difficulty(this.difficulty)
                .accuracy(this.accuracy)
                .build();
    }
}
