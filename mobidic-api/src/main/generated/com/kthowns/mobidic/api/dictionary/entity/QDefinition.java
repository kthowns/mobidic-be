package com.kthowns.mobidic.api.dictionary.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDefinition is a Querydsl query type for Definition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDefinition extends EntityPathBase<Definition> {

    private static final long serialVersionUID = -213616926L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDefinition definition = new QDefinition("definition");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath meaning = createString("meaning");

    public final EnumPath<com.kthowns.mobidic.api.dictionary.type.PartOfSpeech> part = createEnum("part", com.kthowns.mobidic.api.dictionary.type.PartOfSpeech.class);

    public final QWord word;

    public QDefinition(String variable) {
        this(Definition.class, forVariable(variable), INITS);
    }

    public QDefinition(Path<? extends Definition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDefinition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDefinition(PathMetadata metadata, PathInits inits) {
        this(Definition.class, metadata, inits);
    }

    public QDefinition(Class<? extends Definition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new QWord(forProperty("word"), inits.get("word")) : null;
    }

}

