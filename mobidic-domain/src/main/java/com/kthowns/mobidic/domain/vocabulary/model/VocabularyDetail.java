package com.kthowns.mobidic.domain.vocabulary.model;

import lombok.Builder;

@Builder
public record VocabularyDetail(
        Vocabulary vocabulary,
        double learningRate,
        double accuracy
) {
}