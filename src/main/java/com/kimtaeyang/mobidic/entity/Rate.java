package com.kimtaeyang.mobidic.entity;

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
@Table(name="word_rate")
public class Rate {
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