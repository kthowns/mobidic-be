package com.kthowns.mobidic.api.term.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTerm is a Querydsl query type for Term
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerm extends EntityPathBase<Term> {

    private static final long serialVersionUID = 941077777L;

    public static final QTerm term = new QTerm("term");

    public final BooleanPath active = createBoolean("active");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath required = createBoolean("required");

    public final EnumPath<com.kthowns.mobidic.api.term.type.TermType> type = createEnum("type", com.kthowns.mobidic.api.term.type.TermType.class);

    public final StringPath version = createString("version");

    public QTerm(String variable) {
        super(Term.class, forVariable(variable));
    }

    public QTerm(Path<? extends Term> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTerm(PathMetadata metadata) {
        super(Term.class, metadata);
    }

}

