package com.kthowns.mobidic.storage.preset.jpaentity;

import com.kthowns.mobidic.domain.dictionary.model.PartOfSpeech;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "preset_definitions")
public class PresetDefinitionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preset_word_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PresetWordJpaEntity word;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name = "part", nullable = false)
    @Enumerated(EnumType.STRING)
    private PartOfSpeech part;
}