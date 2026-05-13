package com.kthowns.mobidic.api.statistic.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWordStatistic is a Querydsl query type for WordStatistic
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWordStatistic extends EntityPathBase<WordStatistic> {

    private static final long serialVersionUID = 1931870005L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWordStatistic wordStatistic = new QWordStatistic("wordStatistic");

    public final NumberPath<Double> accuracy = createNumber("accuracy", Double.class);

    public final NumberPath<Long> correctCount = createNumber("correctCount", Long.class);

    public final NumberPath<Double> difficulty = createNumber("difficulty", Double.class);

    public final NumberPath<Long> incorrectCount = createNumber("incorrectCount", Long.class);

    public final BooleanPath isLearned = createBoolean("isLearned");

    public final com.kthowns.mobidic.api.dictionary.entity.QWord word;

    public final ComparablePath<java.util.UUID> wordId = createComparable("wordId", java.util.UUID.class);

    public QWordStatistic(String variable) {
        this(WordStatistic.class, forVariable(variable), INITS);
    }

    public QWordStatistic(Path<? extends WordStatistic> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWordStatistic(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWordStatistic(PathMetadata metadata, PathInits inits) {
        this(WordStatistic.class, metadata, inits);
    }

    public QWordStatistic(Class<? extends WordStatistic> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new com.kthowns.mobidic.api.dictionary.entity.QWord(forProperty("word"), inits.get("word")) : null;
    }

}

