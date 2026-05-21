package com.kthowns.mobidic.domain.definition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Definition {
    private UUID id;
    private String meaning;
    private PartOfSpeech part;
}
