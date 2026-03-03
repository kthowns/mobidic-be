package com.kimtaeyang.mobidic.statistic.entity;

import com.kimtaeyang.mobidic.dictionary.entity.Word;
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
    @OneToOne
    @JoinColumn(name = "word_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Word word;

    @Column(name = "correct_count")
    private int correctCount;

    @Column(name = "incorrect_count")
    private int incorrectCount;

    @Setter
    @Column(name = "difficulty")
    @Builder.Default
    private double difficulty = 0.5;

    @Setter
    @Column(name = "accuracy")
    private double accuracy;

    @Column(name = "is_learned")
    private boolean isLearned;

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