package com.kimtaeyang.mobidic.dictionary.dto;

public record VocabularyDetail(
        VocabularyDto vocabulary,
        Double learningRate,
        Double accuracy
) {
}