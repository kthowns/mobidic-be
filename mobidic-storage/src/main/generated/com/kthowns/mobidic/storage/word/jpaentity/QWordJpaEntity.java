package com.kthowns.mobidic.storage.word.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWordJpaEntity is a Querydsl query type for WordJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWordJpaEntity extends EntityPathBase<WordJpaEntity> {

    private static final long serialVersionUID = -1255726245L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWordJpaEntity wordJpaEntity = new QWordJpaEntity("wordJpaEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath expression = createString("expression");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.kthowns.mobidic.storage.vocabulary.jpaentity.QVocabularyJpaEntity vocabulary;

    public QWordJpaEntity(String variable) {
        this(WordJpaEntity.class, forVariable(variable), INITS);
    }

    public QWordJpaEntity(Path<? extends WordJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWordJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWordJpaEntity(PathMetadata metadata, PathInits inits) {
        this(WordJpaEntity.class, metadata, inits);
    }

    public QWordJpaEntity(Class<? extends WordJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vocabulary = inits.isInitialized("vocabulary") ? new com.kthowns.mobidic.storage.vocabulary.jpaentity.QVocabularyJpaEntity(forProperty("vocabulary"), inits.get("vocabulary")) : null;
    }

}

