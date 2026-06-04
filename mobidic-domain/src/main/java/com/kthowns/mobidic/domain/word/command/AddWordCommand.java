package com.kthowns.mobidic.domain.word.command;

public record AddWordCommand(
        String expression
) {
    public static AddWordCommand of(
            String expression
    ) {
        return new AddWordCommand(expression);
    }
}
