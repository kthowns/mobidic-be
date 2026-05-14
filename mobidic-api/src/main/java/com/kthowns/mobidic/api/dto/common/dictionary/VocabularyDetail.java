package com.kthowns.mobidic.api.dto.common.dictionary;

import lombok.Builder;

@Builder
public record VocabularyDetail(
        VocabularyDto vocabulary,
        Double learningRate,
        Double accuracy
) {
}