package com.kthowns.mobidic.api.preset.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetVocabulary is a Querydsl query type for PresetVocabulary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresetVocabulary extends EntityPathBase<PresetVocabulary> {

    private static final long serialVersionUID = 563680445L;

    public static final QPresetVocabulary presetVocabulary = new QPresetVocabulary("presetVocabulary");

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath title = createString("title");

    public final ListPath<PresetWord, QPresetWord> words = this.<PresetWord, QPresetWord>createList("words", PresetWord.class, QPresetWord.class, PathInits.DIRECT2);

    public QPresetVocabulary(String variable) {
        super(PresetVocabulary.class, forVariable(variable));
    }

    public QPresetVocabulary(Path<? extends PresetVocabulary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPresetVocabulary(PathMetadata metadata) {
        super(PresetVocabulary.class, metadata);
    }

}

