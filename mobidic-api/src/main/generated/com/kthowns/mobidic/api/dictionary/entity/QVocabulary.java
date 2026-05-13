package com.kthowns.mobidic.api.dictionary.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVocabulary is a Querydsl query type for Vocabulary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVocabulary extends EntityPathBase<Vocabulary> {

    private static final long serialVersionUID = -126840203L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVocabulary vocabulary = new QVocabulary("vocabulary");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath title = createString("title");

    public final com.kthowns.mobidic.api.user.entity.QUser user;

    public final NumberPath<Long> wordCount = createNumber("wordCount", Long.class);

    public QVocabulary(String variable) {
        this(Vocabulary.class, forVariable(variable), INITS);
    }

    public QVocabulary(Path<? extends Vocabulary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVocabulary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVocabulary(PathMetadata metadata, PathInits inits) {
        this(Vocabulary.class, metadata, inits);
    }

    public QVocabulary(Class<? extends Vocabulary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.kthowns.mobidic.api.user.entity.QUser(forProperty("user")) : null;
    }

}

