package com.kthowns.mobidic.storage.definition.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDefinitionJpaEntity is a Querydsl query type for DefinitionJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDefinitionJpaEntity extends EntityPathBase<DefinitionJpaEntity> {

    private static final long serialVersionUID = -1438001605L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDefinitionJpaEntity definitionJpaEntity = new QDefinitionJpaEntity("definitionJpaEntity");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath meaning = createString("meaning");

    public final EnumPath<com.kthowns.mobidic.domain.definition.model.PartOfSpeech> part = createEnum("part", com.kthowns.mobidic.domain.definition.model.PartOfSpeech.class);

    public final com.kthowns.mobidic.storage.word.jpaentity.QWordJpaEntity word;

    public QDefinitionJpaEntity(String variable) {
        this(DefinitionJpaEntity.class, forVariable(variable), INITS);
    }

    public QDefinitionJpaEntity(Path<? extends DefinitionJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDefinitionJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDefinitionJpaEntity(PathMetadata metadata, PathInits inits) {
        this(DefinitionJpaEntity.class, metadata, inits);
    }

    public QDefinitionJpaEntity(Class<? extends DefinitionJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new com.kthowns.mobidic.storage.word.jpaentity.QWordJpaEntity(forProperty("word"), inits.get("word")) : null;
    }

}

