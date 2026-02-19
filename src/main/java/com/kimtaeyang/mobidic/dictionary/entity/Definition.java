package com.kimtaeyang.mobidic.dictionary.entity;

import com.kimtaeyang.mobidic.dictionary.type.PartOfSpeech;
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
@Table(name="definitions")
public class Definition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "definition")
    private String definition;
    @Column(name = "part")
    @Enumerated(EnumType.STRING)
    private PartOfSpeech part;
}