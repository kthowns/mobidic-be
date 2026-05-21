package com.kthowns.mobidic.storage.preset.jpaentity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetVocabularyJpaEntity is a Querydsl query type for PresetVocabularyJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresetVocabularyJpaEntity extends EntityPathBase<PresetVocabularyJpaEntity> {

    private static final long serialVersionUID = 735701941L;

    public static final QPresetVocabularyJpaEntity presetVocabularyJpaEntity = new QPresetVocabularyJpaEntity("presetVocabularyJpaEntity");

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath title = createString("title");

    public final ListPath<PresetWordJpaEntity, QPresetWordJpaEntity> words = this.<PresetWordJpaEntity, QPresetWordJpaEntity>createList("words", PresetWordJpaEntity.class, QPresetWordJpaEntity.class, PathInits.DIRECT2);

    public QPresetVocabularyJpaEntity(String variable) {
        super(PresetVocabularyJpaEntity.class, forVariable(variable));
    }

    public QPresetVocabularyJpaEntity(Path<? extends PresetVocabularyJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPresetVocabularyJpaEntity(PathMetadata metadata) {
        super(PresetVocabularyJpaEntity.class, metadata);
    }

}

