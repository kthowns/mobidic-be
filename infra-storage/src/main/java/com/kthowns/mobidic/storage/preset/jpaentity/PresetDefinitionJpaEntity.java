package com.kthowns.mobidic.storage.preset.jpaentity;

import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "preset_definitions")
public class PresetDefinitionJpaEntity extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
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