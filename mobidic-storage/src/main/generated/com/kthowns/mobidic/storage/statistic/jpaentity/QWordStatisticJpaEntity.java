package com.kthowns.mobidic.storage.statistic.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWordStatisticJpaEntity is a Querydsl query type for WordStatisticJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWordStatisticJpaEntity extends EntityPathBase<WordStatisticJpaEntity> {

    private static final long serialVersionUID = 1861632033L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWordStatisticJpaEntity wordStatisticJpaEntity = new QWordStatisticJpaEntity("wordStatisticJpaEntity");

    public final NumberPath<Double> accuracy = createNumber("accuracy", Double.class);

    public final NumberPath<Long> correctCount = createNumber("correctCount", Long.class);

    public final NumberPath<Double> difficulty = createNumber("difficulty", Double.class);

    public final NumberPath<Long> incorrectCount = createNumber("incorrectCount", Long.class);

    public final BooleanPath isLearned = createBoolean("isLearned");

    public final com.kthowns.mobidic.storage.word.jpaentity.QWordJpaEntity word;

    public final ComparablePath<java.util.UUID> wordId = createComparable("wordId", java.util.UUID.class);

    public QWordStatisticJpaEntity(String variable) {
        this(WordStatisticJpaEntity.class, forVariable(variable), INITS);
    }

    public QWordStatisticJpaEntity(Path<? extends WordStatisticJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWordStatisticJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWordStatisticJpaEntity(PathMetadata metadata, PathInits inits) {
        this(WordStatisticJpaEntity.class, metadata, inits);
    }

    public QWordStatisticJpaEntity(Class<? extends WordStatisticJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new com.kthowns.mobidic.storage.word.jpaentity.QWordJpaEntity(forProperty("word"), inits.get("word")) : null;
    }

}

