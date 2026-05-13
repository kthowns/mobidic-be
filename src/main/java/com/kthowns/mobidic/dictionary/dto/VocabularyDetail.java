package com.kthowns.mobidic.dictionary.dto;

import lombok.Builder;

@Builder
public record VocabularyDetail(
        VocabularyDto vocabulary,
        Double learningRate,
        Double accuracy
) {
}