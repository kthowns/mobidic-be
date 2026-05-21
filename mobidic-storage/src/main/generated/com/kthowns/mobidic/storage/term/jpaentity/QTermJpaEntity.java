package com.kthowns.mobidic.storage.term.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTermJpaEntity is a Querydsl query type for TermJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTermJpaEntity extends EntityPathBase<TermJpaEntity> {

    private static final long serialVersionUID = -917009509L;

    public static final QTermJpaEntity termJpaEntity = new QTermJpaEntity("termJpaEntity");

    public final BooleanPath active = createBoolean("active");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath required = createBoolean("required");

    public final EnumPath<com.kthowns.mobidic.domain.term.model.TermType> type = createEnum("type", com.kthowns.mobidic.domain.term.model.TermType.class);

    public final StringPath version = createString("version");

    public QTermJpaEntity(String variable) {
        super(TermJpaEntity.class, forVariable(variable));
    }

    public QTermJpaEntity(Path<? extends TermJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTermJpaEntity(PathMetadata metadata) {
        super(TermJpaEntity.class, metadata);
    }

}

