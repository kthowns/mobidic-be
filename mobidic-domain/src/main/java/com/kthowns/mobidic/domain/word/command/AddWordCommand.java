package com.kthowns.mobidic.domain.word.command;

import java.util.UUID;

public record AddWordCommand(
        UUID userId,
        UUID vocabId,
        String expression
) {
    public static AddWordCommand of(
            UUID userId,
            UUID vocabId,
            String expression
    ) {
        return new AddWordCommand(userId, vocabId, expression);
    }
}
