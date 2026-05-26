package com.kthowns.mobidic.domain.vocabulary.model;

public record VocabularyDetail(
        Vocabulary vocabulary,
        double learningRate,
        double accuracy
) {
}
