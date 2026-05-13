package com.kthowns.mobidic.api.preset.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetDefinition is a Querydsl query type for PresetDefinition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresetDefinition extends EntityPathBase<PresetDefinition> {

    private static final long serialVersionUID = 476903722L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresetDefinition presetDefinition = new QPresetDefinition("presetDefinition");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath meaning = createString("meaning");

    public final EnumPath<com.kthowns.mobidic.api.dictionary.type.PartOfSpeech> part = createEnum("part", com.kthowns.mobidic.api.dictionary.type.PartOfSpeech.class);

    public final QPresetWord word;

    public QPresetDefinition(String variable) {
        this(PresetDefinition.class, forVariable(variable), INITS);
    }

    public QPresetDefinition(Path<? extends PresetDefinition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresetDefinition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresetDefinition(PathMetadata metadata, PathInits inits) {
        this(PresetDefinition.class, metadata, inits);
    }

    public QPresetDefinition(Class<? extends PresetDefinition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new QPresetWord(forProperty("word"), inits.get("word")) : null;
    }

}

