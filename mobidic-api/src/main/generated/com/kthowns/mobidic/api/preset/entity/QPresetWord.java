package com.kthowns.mobidic.api.preset.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetWord is a Querydsl query type for PresetWord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresetWord extends EntityPathBase<PresetWord> {

    private static final long serialVersionUID = 819263521L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresetWord presetWord = new QPresetWord("presetWord");

    public final ListPath<PresetDefinition, QPresetDefinition> definitions = this.<PresetDefinition, QPresetDefinition>createList("definitions", PresetDefinition.class, QPresetDefinition.class, PathInits.DIRECT2);

    public final StringPath expression = createString("expression");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QPresetVocabulary vocabulary;

    public QPresetWord(String variable) {
        this(PresetWord.class, forVariable(variable), INITS);
    }

    public QPresetWord(Path<? extends PresetWord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresetWord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresetWord(PathMetadata metadata, PathInits inits) {
        this(PresetWord.class, metadata, inits);
    }

    public QPresetWord(Class<? extends PresetWord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vocabulary = inits.isInitialized("vocabulary") ? new QPresetVocabulary(forProperty("vocabulary")) : null;
    }

}

