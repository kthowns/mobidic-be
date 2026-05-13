package com.kthowns.mobidic.statistic.entity;

import com.kthowns.mobidic.dictionary.entity.Word;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "word_statistics")
public class WordStatistic {
    @Id
    private UUID wordId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Word word;

    @Column(name = "correct_count", nullable = false)
    @Builder.Default
    private Long correctCount = 0L;

    @Column(name = "incorrect_count", nullable = false)
    @Builder.Default
    private Long incorrectCount = 0L;

    @Setter
    @Column(name = "difficulty", nullable = false)
    @Builder.Default
    private double difficulty = 0.5;

    @Setter
    @Column(name = "accuracy", nullable = false)
    @Builder.Default
    private double accuracy = 0.0;

    @Column(name = "is_learned", nullable = false)
    @Builder.Default
    private boolean isLearned = false;

    public void increaseCorrectCount() {
        correctCount++;
    }

    public void increaseIncorrectCount() {
        incorrectCount++;
    }

    public void toggleIsLearned() {
        isLearned = !isLearned;
    }
}