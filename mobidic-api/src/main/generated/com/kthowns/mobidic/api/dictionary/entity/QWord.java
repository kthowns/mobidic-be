package com.kthowns.mobidic.api.dictionary.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWord is a Querydsl query type for Word
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWord extends EntityPathBase<Word> {

    private static final long serialVersionUID = -408215591L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWord word = new QWord("word");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath expression = createString("expression");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QVocabulary vocabulary;

    public QWord(String variable) {
        this(Word.class, forVariable(variable), INITS);
    }

    public QWord(Path<? extends Word> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWord(PathMetadata metadata, PathInits inits) {
        this(Word.class, metadata, inits);
    }

    public QWord(Class<? extends Word> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vocabulary = inits.isInitialized("vocabulary") ? new QVocabulary(forProperty("vocabulary"), inits.get("vocabulary")) : null;
    }

}

