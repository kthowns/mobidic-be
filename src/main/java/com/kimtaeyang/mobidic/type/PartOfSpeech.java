package com.kimtaeyang.mobidic.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PartOfSpeech {
    NOUN("명사"),
    PRONOUN("대명사"),
    VERB("동사"),
    ADJECTIVE("형용사"),
    ADVERB("부사"),
    ARTICLE("관사"),
    PREPOSITION("전치사"),
    CONJUNCTION("접속사"),
    INTERJECTION("감탄사");

    private final String name;
}