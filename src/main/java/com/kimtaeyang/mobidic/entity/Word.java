package com.kimtaeyang.mobidic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "vocab_id")
    private Vocab vocab;

    @Column(name = "expression")
    private String expression;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}