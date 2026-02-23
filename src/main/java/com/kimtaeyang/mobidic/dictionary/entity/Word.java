package com.kimtaeyang.mobidic.dictionary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "words")
@EntityListeners(AuditingEntityListener.class)
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "vocabulary_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Vocabulary vocabulary;

    @Column(name = "expression")
    private String expression;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}