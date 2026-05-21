package com.kthowns.mobidic.storage.vocabulary.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVocabularyJpaEntity is a Querydsl query type for VocabularyJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVocabularyJpaEntity extends EntityPathBase<VocabularyJpaEntity> {

    private static final long serialVersionUID = -89266085L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVocabularyJpaEntity vocabularyJpaEntity = new QVocabularyJpaEntity("vocabularyJpaEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath title = createString("title");

    public final com.kthowns.mobidic.storage.user.jpaentity.QUserJpaEntity user;

    public final NumberPath<Long> wordCount = createNumber("wordCount", Long.class);

    public QVocabularyJpaEntity(String variable) {
        this(VocabularyJpaEntity.class, forVariable(variable), INITS);
    }

    public QVocabularyJpaEntity(Path<? extends VocabularyJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVocabularyJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVocabularyJpaEntity(PathMetadata metadata, PathInits inits) {
        this(VocabularyJpaEntity.class, metadata, inits);
    }

    public QVocabularyJpaEntity(Class<? extends VocabularyJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.kthowns.mobidic.storage.user.jpaentity.QUserJpaEntity(forProperty("user")) : null;
    }

}

