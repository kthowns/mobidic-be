package com.kthowns.mobidic.storage.term.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAgreementJpaEntity is a Querydsl query type for UserAgreementJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAgreementJpaEntity extends EntityPathBase<UserAgreementJpaEntity> {

    private static final long serialVersionUID = 2114354934L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAgreementJpaEntity userAgreementJpaEntity = new QUserAgreementJpaEntity("userAgreementJpaEntity");

    public final DateTimePath<java.time.LocalDateTime> agreedAt = createDateTime("agreedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTermJpaEntity term;

    public final com.kthowns.mobidic.storage.user.jpaentity.QUserJpaEntity user;

    public QUserAgreementJpaEntity(String variable) {
        this(UserAgreementJpaEntity.class, forVariable(variable), INITS);
    }

    public QUserAgreementJpaEntity(Path<? extends UserAgreementJpaEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAgreementJpaEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAgreementJpaEntity(PathMetadata metadata, PathInits inits) {
        this(UserAgreementJpaEntity.class, metadata, inits);
    }

    public QUserAgreementJpaEntity(Class<? extends UserAgreementJpaEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.term = inits.isInitialized("term") ? new QTermJpaEntity(forProperty("term")) : null;
        this.user = inits.isInitialized("user") ? new com.kthowns.mobidic.storage.user.jpaentity.QUserJpaEntity(forProperty("user")) : null;
    }

}

