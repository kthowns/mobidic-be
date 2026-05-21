package com.kthowns.mobidic.storage.preset.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetWordJpaEntity is a Querydsl query type for PresetWordJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresetWordJpaEntity extends EntityPathBase<PresetWordJpaEntity> {

    private static final long serialVersionUID = -662083631L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresetWordJpaEntity presetWordJpaEntity = new QPresetWordJpaEntity("presetWordJpaEntity");

    public final ListPath<PresetDefinitionJpaEntity, QPresetDefinitionJpaEntity> definitions = this.<PresetDefinitionJpaEntity, QPresetDefinitionJpaEntity>createList("definitions", PresetDefinitionJpaEntity.class, QPresetDefinitionJpaEntity.class, PathInits.DIRECT2);

    public final StringPath expression = createString("expression");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QPresetVocabularyJpaEntity vocabulary;

    public QPresetWordJpaEntity(String variable) {
        this(PresetWordJpaEntity.class, forVariable(variable), INITS);
    }

    public QPresetWordJpaEntity(Path<? extends PresetWordJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresetWordJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresetWordJpaEntity(PathMetadata metadata, PathInits inits) {
        this(PresetWordJpaEntity.class, metadata, inits);
    }

    public QPresetWordJpaEntity(Class<? extends PresetWordJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vocabulary = inits.isInitialized("vocabulary") ? new QPresetVocabularyJpaEntity(forProperty("vocabulary")) : null;
    }

}

