package com.kimtaeyang.mobidic.statistic.entity;

import com.kimtaeyang.mobidic.dictionary.entity.Word;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="word_statistics")
public class Statistic {
    @Id
    private UUID wordId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "correct_count")
    private int correctCount;

    @Column(name = "incorrect_count")
    private int incorrectCount;

    @Column(name = "is_learned")
    private int isLearned;
}