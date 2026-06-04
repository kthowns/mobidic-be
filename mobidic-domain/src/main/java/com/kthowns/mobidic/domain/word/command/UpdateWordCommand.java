package com.kthowns.mobidic.domain.word.command;

import java.util.UUID;

public record UpdateWordCommand(
        UUID wordId,
        String expression
) {
    public static UpdateWordCommand of(
            UUID wordId,
            String expression
    ) {
        return new UpdateWordCommand(wordId, expression);
    }
}
