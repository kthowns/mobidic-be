package com.kthowns.mobidic.preset.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "preset_vocabularies")
@EntityListeners(AuditingEntityListener.class)
public class PresetVocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "vocabulary", fetch = FetchType.LAZY)
    private List<PresetWord> words;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "description")
    @Builder.Default
    private String description = "(프리셋 입니다. 자유롭게 수정해보세요!)";
}