package com.kthowns.mobidic.api.preset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "preset_words")
@EntityListeners(AuditingEntityListener.class)
public class PresetWord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preset_vocabulary_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PresetVocabulary vocabulary;

    @OneToMany(mappedBy = "word", fetch = FetchType.LAZY)
    private List<PresetDefinition> definitions;

    @Column(name = "expression", nullable = false)
    private String expression;
}