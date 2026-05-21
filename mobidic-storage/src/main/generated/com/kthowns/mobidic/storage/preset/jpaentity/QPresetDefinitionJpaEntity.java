package com.kthowns.mobidic.storage.preset.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetDefinitionJpaEntity is a Querydsl query type for PresetDefinitionJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresetDefinitionJpaEntity extends EntityPathBase<PresetDefinitionJpaEntity> {

    private static final long serialVersionUID = 443265512L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresetDefinitionJpaEntity presetDefinitionJpaEntity = new QPresetDefinitionJpaEntity("presetDefinitionJpaEntity");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath meaning = createString("meaning");

    public final EnumPath<com.kthowns.mobidic.domain.definition.model.PartOfSpeech> part = createEnum("part", com.kthowns.mobidic.domain.definition.model.PartOfSpeech.class);

    public final QPresetWordJpaEntity word;

    public QPresetDefinitionJpaEntity(String variable) {
        this(PresetDefinitionJpaEntity.class, forVariable(variable), INITS);
    }

    public QPresetDefinitionJpaEntity(Path<? extends PresetDefinitionJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresetDefinitionJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresetDefinitionJpaEntity(PathMetadata metadata, PathInits inits) {
        this(PresetDefinitionJpaEntity.class, metadata, inits);
    }

    public QPresetDefinitionJpaEntity(Class<? extends PresetDefinitionJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new QPresetWordJpaEntity(forProperty("word"), inits.get("word")) : null;
    }

}

